package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
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
    DatabaseHelper.getInstance().paySubscription(player, Integer.parseInt(args[1]));
  }
}
