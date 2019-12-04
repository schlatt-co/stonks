package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PaySubscriptionCommandSub extends CommandSub {

  public PaySubscriptionCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2) {
      sendMessage(player, "Correct usage /" + alias + " paysubscription <service_id>");
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Correct usage /" + alias + " paysubscription <service_id>");
      return;
    }
    Service service = Repo.getInstance().services().get(Integer.parseInt(args[1]));
    if (service == null) {
      sendMessage(player, "Service id not found");
      return;
    }
    paySubscription(player, service);
  }

  private void paySubscription(Player player, Service service) {
    Subscription subscription = service.getSubscription(player);
    if (subscription == null) {
      sendMessage(player, "You are not subscribed to this service");
      return;
    }

    if (!Subscription.isOverdue(service, subscription)) {
      sendMessage(player, "The subscription is not overdue, you don't need to pay for it");
      return;
    }

    new ConfirmationGui.Builder()
        .title("Pay bill of $" + service.cost)
        .onChoiceMade(c -> {
              if (!c) return;
              if (!Stonks.economy.withdrawPlayer(player, service.cost).transactionSuccess()) {
                sendMessage(player, "Insufficient funds!");
              } else {
                //Payment success
                Repo.getInstance().paySubscription(player.getUniqueId(), subscription, service);
                //Subscription created!
                sendMessage(player, "You have resubscribed to the service " + service.name + "!");
                sendMessage(player, "This service will expire in " + service.duration + " days");
                sendMessage(player, "Your subscription will automatically renew, so you don't need to do anything.");
              }
            }
        ).open(player);
  }
}
