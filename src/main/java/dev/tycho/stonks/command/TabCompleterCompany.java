package dev.tycho.stonks.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleterCompany implements TabCompleter {

  private static final List<String> SUBCOMMANDS = Arrays.asList(
      "create",
      "invite",
      "setlogo",
      "invites",
      "subscriptions",
      "list",
      "createservice",
      "setservicemax",
      "createaccount",
      "createholding",
      "removeholding",
      "pay",
      "withdraw",
      "fees",
      "history",
      "top");
  private static final List<String> ADMIN_SUBCOMMANDS = Arrays.asList(
      "hide",
      "unhide",
      "listhidden",
      "verify",
      "unverify",
      "rename");
  private static final List<String> RATIOS = Arrays.asList(
      "0.5",
      "1",
      "1.5",
      "3");
  private static final List<String> AMOUNTS = Arrays.asList(
      "1",
      "10",
      "1000",
      "10000");
  private static final List<String> DURATIONS = Arrays.asList(
      "1",
      "2",
      "7",
      "14");
  private static final List<String> MAX_SUBSCRIBERS = Arrays.asList(
      "0",
      "1",
      "5",
      "10",
      "20");
  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    List<String> completions = new ArrayList<>();
    if (args.length == 1) {
      StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, completions);
      if (sender.isOp() || sender.hasPermission("trevor.mod"))
        StringUtil.copyPartialMatches(args[0], ADMIN_SUBCOMMANDS, completions);
    } else if (args.length == 2) {
      if (args[0].equals("createholding") || args[0].equals("invite") || args[0].equals("memberinfo")) {
        List<String> playerNames = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(o -> playerNames.add(o.getName()));
        StringUtil.copyPartialMatches(args[1], playerNames, completions);
      } else if (args[0].equals("pay") || args[0].equals("withdraw")) {
        StringUtil.copyPartialMatches(args[1], AMOUNTS, completions);
      } else if (args[0].equals("createservice")) {
        StringUtil.copyPartialMatches(args[1], DURATIONS, completions);
      }
    } else if (args.length == 3) {
      if (args[0].equals("createholding")) {
        StringUtil.copyPartialMatches(args[2], RATIOS, completions);
      } else if (args[0].equals("createservice")) {
        StringUtil.copyPartialMatches(args[2], AMOUNTS, completions);
      }
    } else if (args.length == 4) {
      if (args[0].equals("createservice")) {
        StringUtil.copyPartialMatches(args[3], MAX_SUBSCRIBERS, completions);
      }
    }
    Collections.sort(completions);
    return completions;
  }
}
