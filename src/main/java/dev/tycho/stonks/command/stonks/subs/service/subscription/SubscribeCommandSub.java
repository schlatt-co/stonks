package dev.tycho.stonks.command.stonks.subs.service.subscription;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ArgumentStore;
import dev.tycho.stonks.command.base.validators.ServiceValidator;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.entity.Player;

public class SubscribeCommandSub extends ModularCommandSub {

  public SubscribeCommandSub() {
    super(new ServiceValidator("service"));
  }


  @Override
  public void execute(Player player, ArgumentStore store) {
    subscribe(player, getArgument("service", store), true);
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
        .yes(() -> {
              //We can now subscribe to the service
              //Setup the subscription
              if (!Stonks.economy.withdrawPlayer(player, service.cost).transactionSuccess()) {
                sendMessage(player, "Insufficient funds!");
              } else {
                //Payment success
                Subscription subscription = Repo.getInstance().createSubscription(player, service, autoPay);
                Repo.getInstance().paySubscription(player.getUniqueId(), subscription, service);
                //Subscription created!
                sendMessage(player, "You have subscribed to the service " + service.name);
                sendMessage(player, "This service will expire in " + service.duration + " days");
                //tell all managers of the company for the service this user just subscribed

                Repo.getInstance().sendMessageToAllOnlineManagers(Repo.getInstance().companies().get(Repo.getInstance().accountWithId(service.accountPk).companyPk), player.getName() + " has subscribed to the " + service.name + " service!");
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
