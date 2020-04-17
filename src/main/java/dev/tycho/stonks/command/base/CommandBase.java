package dev.tycho.stonks.command.base;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandBase implements CommandExecutor, TabCompleter {

  private static JavaPlugin plugin;
  private HashMap<String, CommandSub> subCommands;

  public CommandBase(String name, CommandSub defaultCommand) {
    subCommands = new HashMap<>();
    subCommands.put("default", defaultCommand);
    PluginCommand command = plugin.getCommand(name);
    if (command == null) {
      throw new RuntimeException("Invalid command name: " + name);
    }
    command.setExecutor(this);
    command.setTabCompleter(this);
  }

  public static void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }

  public boolean isSubCommand(String alias) {
    return subCommands.containsKey(alias);
  }

  public boolean addSubCommand(String alias, CommandSub commandSub) {
    return subCommands.put(alias, commandSub) != null;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command can only used by a player!");
      return true;
    }
    Player player = (Player) sender;
    if (args.length == 0) {
      subCommands.get("default").onCommand(player, label, args);
    } else if (subCommands.containsKey(args[0])) {
      CommandSub sub = subCommands.get(args[0]);
      if (sub.hasPermission(player)) {
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
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
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
        CommandSub commandSub = subCommands.get(subcommandName);
        if (commandSub.hasPermission(player)) {
          matches.add(subcommandName);
        }
      }
    }
    return matches;
  }

  public static void setPlugin(JavaPlugin plugin) {
    CommandBase.plugin = plugin;
  }
}
