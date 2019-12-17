package dev.tycho.stonks.gui;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PlayerSubscriptionListGui extends CollectionGui<Subscription> {

  public PlayerSubscriptionListGui(Collection<Subscription> subscriptions) {
    super(subscriptions, "Your Subscriptions");
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {

  }

  //TODO remove database calls from here
  @Override
  protected ClickableItem itemProvider(Player player, Subscription obj) {
    Service service = Repo.getInstance().services().get(obj.servicePk);
    Company company = Repo.getInstance().companies().get(Repo.getInstance().accountWithId(service.accountPk).companyPk);
    if (Subscription.isOverdue(service, obj)) {
      return ClickableItem.of(ItemInfoHelper.subscriptionDisplayItem(obj, service, company,
          ChatColor.GREEN + "Left click to pay subscription",
          ChatColor.RED + "Right click to cancel subscription"),
          e -> {
            if (e.getClick().isLeftClick()) {
              player.performCommand("stonks paysubscription " + obj.servicePk);
            } else if (e.getClick().isRightClick()) {
              player.performCommand("stonks unsubscribe " + obj.servicePk);
            }
          });
    } else {
      return ClickableItem.of(ItemInfoHelper.subscriptionDisplayItem(obj, service, company,
          ChatColor.RED + "Right click to cancel subscription"),
          e -> {
            if (e.getClick().isRightClick()) {
              player.performCommand("stonks unsubscribe " + obj.servicePk);
            }
          });
    }
  }
}
