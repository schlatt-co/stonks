package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.gui.SubscriberListGui;
import dev.tycho.stonks.model.service.Service;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubscribersCommandSub extends CommandSub {

  public SubscribersCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2) {
      sendMessage(player, "Correct usage /" + alias + " subscribers <service_id>");
      return;
    }

    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Service Id must be a number");
      return;
    }

    Service service = Repo.getInstance().services().get(Integer.parseInt(args[1]));
    if (service == null) {
      sendMessage(player, "Service ID not found");
      return;
    }

    new SubscriberListGui(service).show(player);
  }
}
