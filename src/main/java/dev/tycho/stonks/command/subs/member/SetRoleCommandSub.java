package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SetRoleCommandSub extends CommandSub {

  private static final List<String> ROLES = Arrays.asList(
      "CEO",
      "Manager",
      "Employee",
      "Intern");

  public SetRoleCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return matchPlayerName(args[1]);
    } else if (args.length == 3) {
      return copyPartialMatches(args[2], ROLES);
    }
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 4) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " setrole <player> <role> <company>");
      return;
    }

    DatabaseHelper.getInstance().setRole(player, args[1], args[2], concatArgs(3, args));
  }
}
