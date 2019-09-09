package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KickMemberCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return matchPlayerName(args[1]);
    }
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 3) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " kickmember <player> <company>");
      return;
    }
    DatabaseHelper.getInstance().kickMember(player, args[1], concatArgs(2, args));
  }
}
