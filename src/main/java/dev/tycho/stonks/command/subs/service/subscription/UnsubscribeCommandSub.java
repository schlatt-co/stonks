package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnsubscribeCommandSub extends CommandSub {

  public UnsubscribeCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2) {
      sendMessage(player, "Correct usage /" + alias + " unsubscribe <service_id>");
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Service ID must be a number");
      return;
    }

    Service service = Repo.getInstance().services().get(Integer.parseInt(args[1]));
    if (service == null) {
      sendMessage(player, "Service id not found");
      return;
    }

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
        }).open(player);

  }
}
