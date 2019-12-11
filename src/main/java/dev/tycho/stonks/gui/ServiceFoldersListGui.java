package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.CompanyAccount;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServiceFoldersListGui extends CollectionGui<Account> {
  private Company company;

  public ServiceFoldersListGui(Company company) {
    super(separateFolders(company), company.name + " service folders");
    this.company = company;
  }

  private static Collection<Account> separateFolders(Company company) {
    List<Account> folders = new ArrayList<>();
    for (Account a : company.accounts) {
      if (a.services.size() > 0) {
        folders.add(a);
      }
    }
    return folders;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"), e -> player.performCommand("stonks info " + company.name)));
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.logoMaterial), company.name)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, Account account) {
    ReturningAccountVisitor<Material> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        val = Material.CHEST;
      }

      @Override
      public void visit(HoldingsAccount a) {
        val = Material.CHEST;
      }
    };
    account.accept(visitor);

    return ClickableItem.of(
        Util.item(visitor.getRecentVal(), ChatColor.YELLOW + account.name, "Click to see services"),
        e ->
            player.performCommand("stonks services " + account.pk));
  }
}
