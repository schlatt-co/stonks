package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServiceFoldersListGui extends CollectionGuiBase<AccountLink> {
  private Company company;

  public ServiceFoldersListGui(Company company) {
    super(separateFolders(company.getServices()), company.getName() + " service folders");
    this.company = company;
  }

  private static Collection<AccountLink> separateFolders(Collection<Service> services) {
    List<Integer> linkIds = new ArrayList<>();
    Collection<AccountLink> folders = new ArrayList<>();
    for (Service service : services) {
      if (!linkIds.contains(service.getAccount().getId())) {
        folders.add(service.getAccount());
        linkIds.add(service.getAccount().getId());
      }
    }
    return folders;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"), e -> player.performCommand("stonks info " + company.getName())));
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));
  }

  @Override
  protected ClickableItem itemProvider(Player player, AccountLink obj) {
    Account account = obj.getAccount();
    ReturningAccountVisitor<Material> visitor = new ReturningAccountVisitor<Material>() {
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
        Util.item(visitor.getRecentVal(), ChatColor.YELLOW + account.getName(), "Click to see services"),
        e ->
            player.performCommand("stonks services " + obj.getId()));
  }
}
