package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.*;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemInfoHelper {

  public static ItemStack companyDisplayItem(Company company) {
    String companyDisplayName = company.getName();
    if (company.isVerified()) companyDisplayName += "  " + ChatColor.AQUA + "✔";
    return Util.item(Material.getMaterial(company.getLogoMaterial()), companyDisplayName,
        (company.isVerified())? ChatColor.ITALIC + "" + ChatColor.AQUA + "Verified company" : "",
        "Total value: " + ChatColor.GREEN + "$" + company.getTotalValue(),
        "Members: " + company.getNumAcceptedMembers(),
        "Accounts: " + company.getAccounts().size()
    );
  }

  public static ItemStack accountDisplayItem(AccountLink link) {
    return accountDisplayItem(link, new String[]{});
  }


  public static ItemStack accountDisplayItem(AccountLink link, String... extraLore) {

    ReturningAccountVisitor<ItemStack> visitor = new ReturningAccountVisitor<ItemStack>() {
      @Override
      public void visit(CompanyAccount a) {
        List<String> lore = new ArrayList<>();
        lore.add("ID: " + ChatColor.YELLOW + link.getId());
        lore.add("Balance: " + ChatColor.GREEN + " $" + a.getTotalBalance());
        lore.add(ChatColor.ITALIC + "Company Account");
        if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
        val = Util.item(Material.DIAMOND, a.getName(), lore);
      }

      @Override
      public void visit(HoldingsAccount a) {
        List<String> lore = new ArrayList<>();
        lore.add("ID: " + ChatColor.YELLOW + link.getId());
        lore.add("Total Balance: " + ChatColor.GREEN + "$" + a.getTotalBalance());
        lore.add("Holdings: " + ChatColor.YELLOW + a.getHoldings().size());
        lore.add(ChatColor.ITALIC + "Holdings Account");
        if (extraLore.length > 0) lore.addAll(Arrays.asList(extraLore));
        val = Util.item(Material.GOLD_INGOT, a.getName(), lore);
      }
    };
    link.getAccount().accept(visitor);
    return visitor.getRecentVal();
  }

}
