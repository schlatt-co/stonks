package dev.tycho.stonks.command.subs.holding;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveHoldingCommandSub extends CommandSub {

  public RemoveHoldingCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 3) {
      return matchPlayerName(args[2]);
    }
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 3) {
      sendMessage(player, "Correct user: " + ChatColor.YELLOW + "/" + alias + " removeholding <account id> <player>");
      return;
    }

    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Correct user: " + ChatColor.YELLOW + "/" + alias + " removeholding <account id> <player>");
      return;
    }

    DatabaseHelper.getInstance().removeHolding(player, Integer.parseInt(args[1]), args[2]);
  }
}
