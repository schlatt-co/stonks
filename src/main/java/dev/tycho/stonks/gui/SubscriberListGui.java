package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class SubscriberListGui extends CollectionGui<Subscription> {

  private Service service;

  public SubscriberListGui(Service service) {
    super(service.subscriptions, service.name + " Subscriptions");
    this.service = service;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to services"),
        e -> player.performCommand("stonks services " + service.accountPk)));
    contents.set(0, 4, ClickableItem.empty(ItemInfoHelper.serviceDisplayItem(service)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, Subscription obj) {
    double dayDiff = Subscription.getDaysOverdue(service, obj);
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(obj.playerUUID);
    return ClickableItem.empty(Util.playerHead(offlinePlayer.getName(), offlinePlayer,
        new DecimalFormat("#.#").format(Math.abs(dayDiff)) + " days " + ((dayDiff > 0) ? "overdue" : "remaining")
    ));
  }
}
