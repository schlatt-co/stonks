package dev.tycho.stonks.command.base;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandBase implements CommandExecutor, TabCompleter {

  private HashMap<String, SubCommand> subCommands;

  public CommandBase(SubCommand defaultCommand) {
    subCommands = new HashMap<>();
    subCommands.put("default", defaultCommand);
  }

  public boolean addSubCommand(String alias, SubCommand subCommand) {
    if (subCommands.containsKey(alias)) return false;
    subCommands.put(alias, subCommand);
    return true;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command can only used by a player!");
      return true;
    }
    Player player = (Player) sender;
    if (args.length == 0) {
      subCommands.get("default").onCommand(player, label, args);
    } else if (subCommands.containsKey(args[0])) {
      SubCommand sub = subCommands.get(args[0]);
      if (sub.getPermission() != null && !player.hasPermission(sub.getPermission())) {
        sendMessage(player, "You have insufficient permissions to execute this command!");
        return true;
      }
      subCommands.get(args[0]).onCommand(player, label, args);
    } else {
      sendMessage(sender, "Unknown sub-command! Do " + ChatColor.YELLOW + "/" + label + " help" + ChatColor.GREEN + " for help.");
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    if (!(sender instanceof Player)) return null;
    Player player = (Player) sender;
    // If we have no subcommand yet, show a list of possible completions
    if (args.length == 1) {
      return subCommandCompletions(player, args[0]);
    } else if (args.length > 1) {
      // Else get the completion list from the subcommand
      String arg = args[0];
      if (subCommands.containsKey(arg)) {
        List<String> comp = subCommands.get(arg).getTabCompletions(player, args);
        // Avoid returning a null list since this causes MC to show a list of players as autocomplete options
        return comp == null ? new ArrayList<>() : comp;
      }
    }
    return new ArrayList<>();
  }

  private List<String> subCommandCompletions(Player player, String arg) {
    ArrayList<String> matches = new ArrayList<>();
    // Return the names of all subcommands that match what the player has entered
    for (String subcommandName : subCommands.keySet()) {
      if (subcommandName.contains(arg)) {
        SubCommand subCommand = subCommands.get(subcommandName);
        if (subCommand.getPermission() == null || player.hasPermission(subCommand.getPermission())) {
          matches.add(subcommandName);
        }
      }
    }
    return matches;
  }


  public static void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }
}
