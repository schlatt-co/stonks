package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ServiceValidator;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.service.Service;
import org.bukkit.entity.Player;

public class SubscribeCommandSub extends ModularCommandSub {

  public SubscribeCommandSub() {
    super(new ServiceValidator("service"));
  }


  @Override
  public void execute(Player player) {
    subscribe(player, getArgument("service"), true);
//    List<String> info = new ArrayList<>();
//    info.add("Automatic billing will automatically renew your");
//    info.add("subscription to this service.");
//    info.add("If you choose NO you will have to manually");
//    info.add("resubscribe each duration.");
//    info.add("You can cancel your subscription at any time");
//    new ConfirmationGui.Builder().title("Set up automatic billing?")
//        .info(info)
//        .onChoiceMade(c ->
//        ).open(player);
  }

  private void subscribe(Player player, Service service, boolean autoPay) {
    if (service.maxSubscribers > 0 && service.subscriptions.size() >= service.maxSubscribers) {
      sendMessage(player, "That service has the maximum number of subscriptions");
      return;
    }

    if (service.getSubscription(player) != null) {
      sendMessage(player, "You cannot subscribe to the same service twice");
      return;
    }
    new ConfirmationGui.Builder()
        .title("Accept first bill of $" + service.cost)
        .onChoiceMade(c -> {
              if (!c) return;
              //We can now subscribe to the service
              //Setup the subscription
              if (!Stonks.economy.withdrawPlayer(player, service.cost).transactionSuccess()) {
                sendMessage(player, "Insufficient funds!");
              } else {
                //Payment success
                Repo.getInstance().createSubscription(player, service, autoPay);
                Repo.getInstance().createTransaction(player.getUniqueId(), Repo.getInstance().accountWithId(service.accountPk),
                    "First Subscription payment (" + service.name + ")", service.cost);
                //Subscription created!
                sendMessage(player, "You have subscribed to the service " + service.name);
                sendMessage(player, "This service will expire in " + service.duration + " days");
                if (autoPay) {
                  sendMessage(player, "Your subscription will automatically renew, so you don't need to do anything.");
                } else {
                  sendMessage(player, "You have set your subscription to manually renew, so you will need to resubscribe in " + service.duration + " days time.");
                }
              }
            }
        ).show(player);
  }
}
