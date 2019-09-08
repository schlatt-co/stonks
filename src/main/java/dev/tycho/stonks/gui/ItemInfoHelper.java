package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.AccountLink;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.CompanyAccount;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import dev.tycho.stonks.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemInfoHelper {

  static ItemStack companyDisplayItem(Company company) {
    String companyDisplayName = company.getName();
    if (company.isVerified()) companyDisplayName += "  " + ChatColor.AQUA + "✔";
    return Util.item(Material.getMaterial(company.getLogoMaterial()), companyDisplayName,
        (company.isVerified()) ? ChatColor.ITALIC + "" + ChatColor.AQUA + "Verified company" : "",
        "Total value: " + ChatColor.GREEN + "$" + Util.commify(company.getTotalValue()),
        "Members: " + company.getNumAcceptedMembers(),
        "Accounts: " + company.getAccounts().size()
    );
  }

  static ItemStack accountDisplayItem(AccountLink link) {
    return accountDisplayItem(link, new String[]{});
  }


  static ItemStack accountDisplayItem(AccountLink link, String... extraLore) {

    ReturningAccountVisitor<ItemStack> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        List<String> lore = new ArrayList<>();
        lore.add("ID: " + ChatColor.YELLOW + link.getId());
        lore.add("Balance: " + ChatColor.GREEN + " $" + Util.commify(a.getTotalBalance()));
        lore.add(ChatColor.ITALIC + "Company Account");
        if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
        val = Util.item(Material.DIAMOND, a.getName(), lore);
      }

      @Override
      public void visit(HoldingsAccount a) {
        List<String> lore = new ArrayList<>();
        lore.add("ID: " + ChatColor.YELLOW + link.getId());
        lore.add("Total Balance: " + ChatColor.GREEN + "$" + Util.commify(a.getTotalBalance()));
        lore.add("Holdings: " + ChatColor.YELLOW + a.getHoldings().size());
        lore.add(ChatColor.ITALIC + "Holdings Account");
        if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
        val = Util.item(Material.GOLD_INGOT, a.getName(), lore);
      }
    };
    link.getAccount().accept(visitor);
    return visitor.getRecentVal();
  }

  static ItemStack transactionDisplayItem(Transaction transaction) {
    List<String> lore = new ArrayList<>();
    if (transaction.getPayee() != null) {
      OfflinePlayer p = Bukkit.getOfflinePlayer(transaction.getPayee());
      if (p.hasPlayedBefore()) lore.add("Made by " + ChatColor.YELLOW + p.getName());
    }
    lore.add("On (EST) " + transaction.getTimestamp().toString());
    if (transaction.getMessage() != null) {
      lore.add("Message: ");
      lore.add(ChatColor.ITALIC + transaction.getMessage());
    }

    Material itemMaterial = Material.CHEST;
    if (transaction.getPayee() != null) {
      itemMaterial = (transaction.getAmount() > 0) ? Material.GREEN_WOOL : Material.RED_WOOL;
      if (transaction.getMessage().startsWith("Subscription")) itemMaterial = Material.KNOWLEDGE_BOOK;
    }

    return Util.item(itemMaterial,
        ((transaction.getAmount() > 0) ? ChatColor.GREEN : ChatColor.RED) + "$" + transaction.getAmount(),
        lore);
  }

  static ItemStack serviceDisplayItem(Service service, String... extraLore) {
    List<String> lore = new ArrayList<>();
    lore.add("Cost: " + ChatColor.GREEN + "$" + service.getCost());
    lore.add("Subscribers: " + ChatColor.YELLOW + service.getSubscriptions().size() + "/" +
        ((service.getMaxSubscriptions() > 0) ? service.getMaxSubscriptions() : "unlimited"));
    lore.add("Subscription Period: " + new DecimalFormat("#.#").format(service.getDuration()) + " days");
    if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
    Material itemMaterial = Material.KNOWLEDGE_BOOK;
    return Util.item(itemMaterial, service.getName(), lore);
  }

  static ItemStack subscriptionDisplayItem(Subscription subscription, String... extraLore) {
    Service service = subscription.getService();
    boolean overdue = subscription.isOverdue();
    List<String> lore = new ArrayList<>();
    lore.add("Company: " + ChatColor.YELLOW + service.getCompany().getName());
    lore.add((overdue ? ChatColor.RED : ChatColor.GREEN) +
        new DecimalFormat("#.#").format(Math.abs(subscription.getDaysOverdue())) + ChatColor.WHITE + " days " + (overdue ? "overdue" : "remaining"));
    lore.add("Subscription cost: " + ChatColor.GREEN + "$" + service.getCost());
    if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
    return Util.item(Material.getMaterial(service.getCompany().getLogoMaterial()),
        service.getName(),
        lore);
  }

}
