package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.validators.ServiceValidator;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.entity.Player;

public class PaySubscriptionSubCommand extends ModularSubCommand {

  public PaySubscriptionSubCommand() {
    super(new ServiceValidator("service"));
  }

  @Override
  public void execute(Player player) {
    paySubscription(player, getArgument("service"));
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
        .yes(() -> {
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
        ).show(player);
  }
}
