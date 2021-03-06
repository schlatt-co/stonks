package dev.tycho.stonks.gui;

import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.api.perks.CompanyPerkAction;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import dev.tycho.stonks.util.Util;
import io.github.jroy.pluginlibrary.PluginLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemInfoHelper {

  static ItemStack companyDisplayItem(Company company) {
    String companyDisplayName = company.name;
    if (company.verified) companyDisplayName += "  " + ChatColor.AQUA + "✔";
    return Util.item(Material.getMaterial(company.logoMaterial), companyDisplayName,
        (company.verified) ? ChatColor.ITALIC + "" + ChatColor.AQUA + "Verified company" : "",
        "Total value: " + ChatColor.GREEN + "$" + Util.commify(company.getTotalValue()),
        "Members: " + company.getNumAcceptedMembers(),
        "Accounts: " + company.accounts.size()
    );
  }

  static ItemStack accountDisplayItem(Account account, Player player) {
    return accountDisplayItem(account, player, new String[]{});
  }

  static ItemStack perkDisplayItem(Company company, CompanyPerk perk) {
    ItemStack itemStack = Util.item(perk.getIcon(), perk.getName(), "", ChatColor.translateAlternateColorCodes('&', "&fPlugin: &e" + perk.getPlugin().getName()), ChatColor.translateAlternateColorCodes('&', (company.ownsPerk(perk.getNamespace()) ? "&ePurchased!" : "&fPrice: &e" + perk.getPrice())), "");
    ItemMeta meta = itemStack.getItemMeta();
    assert meta != null;
    List<String> lore = meta.getLore();
    assert lore != null;
    if (!company.ownsPerk(perk.getNamespace()) && (perk.isVerifiedOnly() && !company.verified)) {
      lore.add(ChatColor.RED + "Only verified companies can buy");
      lore.add(ChatColor.RED + "this perk! Have an Admin verify");
      lore.add(ChatColor.RED + "this company.");
      lore.add("");
    }
    for (String curString : perk.getDescription()) {
      lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', curString));
    }
    meta.setLore(lore);
    if (company.ownsPerk(perk.getNamespace())) {
      meta.addEnchant(PluginLibrary.glowEnchantment, 1, true);
    }
    itemStack.setItemMeta(meta);
    return itemStack;
  }

  static ItemStack perkActionDisplayItem(Company company, Player player, CompanyPerkAction perkAction) {
    Member member = company.getMember(player);
    if (member == null) {
      player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "You're not a member for this organization. You should never see this, please report this!");
      throw new RuntimeException("Company member made illegal access to perk action!");
    }
    ItemStack itemStack = Util.item(perkAction.getIcon(), perkAction.getName(), "");
    ItemMeta meta = itemStack.getItemMeta();
    assert meta != null;
    List<String> lore = meta.getLore();
    for (String curString : perkAction.getDescription()) {
      assert lore != null;
      lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', curString));
    }
    meta.setLore(lore);
    if (member.role.hasPermission(perkAction.getPermissionLevel())) {
      meta.addEnchant(PluginLibrary.glowEnchantment, 1, true);
    }
    itemStack.setItemMeta(meta);
    return itemStack;
  }


  static ItemStack accountDisplayItem(Account account, Player player, String... extraLore) {

    ReturningAccountVisitor<ItemStack> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        List<String> lore = new ArrayList<>();
        lore.add("ID: " + ChatColor.YELLOW + a.pk);
        lore.add("Balance: " + ChatColor.GREEN + " $" + Util.commify(a.getTotalBalance()));
        lore.add(ChatColor.ITALIC + "Company Account");
        if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
        val = Util.item(Material.DIAMOND, a.name, lore);
      }

      @Override
      public void visit(HoldingsAccount a) {
        List<String> lore = new ArrayList<>();
        lore.add("ID: " + ChatColor.YELLOW + a.pk);
        lore.add("Total Balance: " + ChatColor.GREEN + "$" + Util.commify(a.getTotalBalance()));
        lore.add("Holdings: " + ChatColor.YELLOW + a.holdings.size());
        lore.add(ChatColor.ITALIC + "Holdings Account");
        if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));

        Material material;
        //If the player has no money in the holding display it as an iron bar
        Holding playerHolding = a.getPlayerHolding(player.getUniqueId());
        if (playerHolding != null && playerHolding.balance > 0.1) {
          material = Material.GOLD_INGOT;
        } else {
          material = Material.IRON_INGOT;
        }

        val = Util.item(material, a.name, lore);
      }
    };
    account.accept(visitor);
    return visitor.getRecentVal();
  }

  static ItemStack transactionDisplayItem(Transaction transaction) {
    List<String> lore = new ArrayList<>();
    if (transaction.payeeUUID != null) {
      OfflinePlayer p = Bukkit.getOfflinePlayer(transaction.payeeUUID);
      if (p.hasPlayedBefore()) lore.add("Made by " + ChatColor.YELLOW + p.getName());
    }
    lore.add("On (EST) " + transaction.timestamp.toString());
    if (transaction.message != null) {
      lore.add("Message: ");
      lore.add(ChatColor.ITALIC + transaction.message);
    }

    Material itemMaterial = Material.CHEST;
    if (transaction.payeeUUID != null) {
      itemMaterial = (transaction.amount > 0) ? Material.GREEN_WOOL : Material.RED_WOOL;

      if (transaction.message != null && transaction.message.startsWith("Subscription"))
        itemMaterial = Material.KNOWLEDGE_BOOK;
    }
    return Util.item(itemMaterial,
        ((transaction.amount > 0) ? ChatColor.GREEN : ChatColor.RED) + "$" + transaction.amount,
        lore);
  }

  static ItemStack serviceDisplayItem(Service service, String... extraLore) {
    List<String> lore = new ArrayList<>();
    lore.add("Cost: " + ChatColor.GREEN + "$" + service.cost);
    lore.add("Subscribers: " + ChatColor.YELLOW + service.subscriptions.size() + "/" +
        ((service.maxSubscribers > 0) ? service.maxSubscribers : "unlimited"));
    lore.add("Subscription Period: " + new DecimalFormat("#.#").format(service.duration) + " days");
    if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
    Material itemMaterial = Material.KNOWLEDGE_BOOK;
    return Util.item(itemMaterial, service.name, lore);
  }

  static ItemStack subscriptionDisplayItem(Subscription subscription, Service service, Company company, String... extraLore) {
    boolean overdue = Subscription.isOverdue(service, subscription);
    List<String> lore = new ArrayList<>();
    lore.add("Company: " + ChatColor.YELLOW + company.name);
    lore.add((overdue ? ChatColor.RED : ChatColor.GREEN) +
        new DecimalFormat("#.#").format(Math.abs(Subscription.getDaysOverdue(service, subscription))) + ChatColor.WHITE + " days " + (overdue ? "overdue" : "remaining"));
    lore.add("Subscription cost: " + ChatColor.GREEN + "$" + service.cost);
    if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
    return Util.item(Material.getMaterial(company.logoMaterial),
        service.name,
        lore);
  }

}
