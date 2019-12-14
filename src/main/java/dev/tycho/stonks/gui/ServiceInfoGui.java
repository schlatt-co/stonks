package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServiceInfoGui extends InventoryGui {
  private Service service;
  private Account account;

  public ServiceInfoGui(Service service, Account account) {
    super(service.name + " Info");
    this.service = service;
    this.account = account;
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
}
