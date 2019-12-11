package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ServiceValidator;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.entity.Player;

public class UnsubscribeCommandSub extends ModularCommandSub {

  public UnsubscribeCommandSub() {
    super(new ServiceValidator("service"));
  }

  @Override
  public void execute(Player player) {
    Service service = getArgument("service");

    Subscription subscription = service.getSubscription(player);
    if (subscription == null) {
      sendMessage(player, "You are not subscribed to this service");
      return;
    }
    new ConfirmationGui.Builder().title("Unsubscribe from " + service.name + "?")
        .onChoiceMade(c -> {
          if (!c) return;
          if (Repo.getInstance().deleteSubscription(subscription, service)) {
            sendMessage(player, "You have unsubscribed from " + service.name);
          } else {
            sendMessage(player, "Error deleting subscription");
          }
        }).show(player);

  }
}
