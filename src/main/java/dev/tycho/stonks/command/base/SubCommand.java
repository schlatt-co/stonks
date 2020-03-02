package dev.tycho.stonks.command.base;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.util.StonksUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

  protected String permission;

  private void setPermission(String permission) {
    this.permission = permission;
  }

  public String getPermission() {
    return permission;
  }

  public SubCommand() {
    this.permission = null;
  }

  public abstract void onCommand(Player player, String alias, String[] args);

  public static SubCommand perms(String p, SubCommand c) {
    c.setPermission(p);
    return c;
  }

  protected static String concatArgs(int startArg, String[] args) {
    StringBuilder concat = new StringBuilder();
    for (int i = startArg; i < args.length; i++) {
      if (i > startArg) concat.append(" ");
      concat.append(args[i]);
    }
    return concat.toString();
  }

  public static void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }

  protected static List<String> copyPartialMatches(String search, Iterable<String> stack) {
    List<String> matches = new ArrayList<>();
    StringUtil.copyPartialMatches(search, stack, matches);
    return matches;
  }

  protected static List<String> matchPlayerName(String search) {
    List<String> playerNames = new ArrayList<>();
    Bukkit.getOnlinePlayers().forEach(o -> playerNames.add(o.getName()));
    return copyPartialMatches(search, playerNames);
  }


  protected static Player playerFromName(String name) {
    StonksUser u = Stonks.getOfflineUser(name);
    return u.getBase();
  }
}

