package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HoldingInfoCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " holdinginfo <account id>");
      return;
    }

    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Invalid account id!");
      return;
    }

    DatabaseHelper.getInstance().openHoldingAccountInfo(player, Integer.parseInt(args[1]));
  }
}
