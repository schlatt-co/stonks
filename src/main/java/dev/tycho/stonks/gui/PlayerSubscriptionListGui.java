package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.service.Subscription;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerSubscriptionListGui extends CollectionGuiBase<Subscription> {
  Player player;

  public PlayerSubscriptionListGui(Player player) {
    super(databaseManager.getSubscriptionDao().getPlayerSubscriptions(player), "Your Subscriptions");
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {

  }

  @Override
  protected ClickableItem itemProvider(Player player, Subscription obj) {
    if (obj.isOverdue()) {
      return ClickableItem.of(ItemInfoHelper.subscriptionDisplayItem(obj,
          ChatColor.GREEN + "Left click to pay subscription",
          ChatColor.RED + "Right click to cancel subscription"),
          e -> {
            if (e.getClick().isLeftClick()) {
              player.performCommand("stonks paysubscription " + obj.getService().getId());
            } else if (e.getClick().isRightClick()) {
              player.performCommand("stonks unsubscribe " + obj.getService().getId());
            }
          });
    } else {
      return ClickableItem.of(ItemInfoHelper.subscriptionDisplayItem(obj,
          ChatColor.RED + "Right click to cancel subscription"),
          e -> {
            if (e.getClick().isRightClick()) {
              player.performCommand("stonks unsubscribe " + obj.getService().getId());
            }
          });
    }
  }
}
