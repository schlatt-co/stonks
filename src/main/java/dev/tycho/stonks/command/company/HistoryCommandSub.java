package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " history [-v] <account id>");
      return;
    }

    if (args.length == 2) {
      return;
    }

    if (args.length > 2) {

    }

  }
}
