package dev.tycho.stonks.gui;

import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServiceInfoGui implements InventoryProvider {

  public static DatabaseManager databaseManager;
  public static InventoryManager inventoryManager;

  private Service service;
  private Account account;
  private ServiceInfoGui(Service service, Account account) {
    this.service = service;
    this.account = account;
  }

  public static SmartInventory getInventory(Service service, Account account) {
    return SmartInventory.builder()
        .id("serviceInfo")
        .provider(new ServiceInfoGui(service, account))
        .manager(inventoryManager)
        .size(5, 9)
        .title(service.name + " Info")
        .build();
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    contents.fillBorders(ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Services List"),
        e -> player.performCommand("stonks services " + service.accountPk)));
    contents.set(0, 4, ClickableItem.empty(ItemInfoHelper.serviceDisplayItem(service)));


    contents.set(2, 3, ClickableItem.of(Util.item(Material.PLAYER_HEAD, "Subscribers"),
        e -> player.performCommand("stonks subscribers " + service.pk)));
    contents.set(2, 5, ClickableItem.of(ItemInfoHelper.accountDisplayItem(account, player),
        e -> player.performCommand("stonks accounts " + account.companyPk)));

  }

  @Override
  public void update(Player player, InventoryContents contents) {

  }
}
