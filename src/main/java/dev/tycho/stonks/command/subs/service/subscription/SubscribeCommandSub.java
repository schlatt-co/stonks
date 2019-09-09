package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubscribeCommandSub extends CommandSub {
  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2) {
      sendMessage(player, "Correct usage: /stonks subscribe <service_id>");
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Correct usage: /stonks subscribe <service_id>");
      return;
    }
    List<String> info = new ArrayList<>();
    info.add("Automatic billing will automatically renew your");
    info.add("subscription to this service.");
    info.add("If you choose NO you will have to manually");
    info.add("resubscribe each duration.");
    info.add("You can cancel your subscription at any time");
    int serviceId = Integer.parseInt(args[1]);
    new ConfirmationGui.Builder().title("Set up automatic billing?")
        .info(info)
        .onChoiceMade(c -> DatabaseHelper.getInstance().subscribeToService(player, serviceId, c)
        ).open(player);
  }
}
