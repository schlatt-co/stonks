package dev.tycho.stonks.command.base;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandBase implements CommandExecutor, TabCompleter {

  private final String name;

  private Map<String, CommandSub> subCommands = new HashMap<>();

  public CommandBase(String name, CommandSub defaultSub) {
    this.name = name;
    addSubCommand("default", defaultSub);
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
    } else if (!subCommands.containsKey(args[0])) {
      subCommands.get(args[0]).onCommand(player, label, args);
    } else {
      sendMessage(sender, "Unknown sub-command! Do " + ChatColor.YELLOW + "/" + label + " help" + ChatColor.GREEN + " for help.");
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    List<String> completions = new ArrayList<>();
    if (args.length == 1) {
      StringUtil.copyPartialMatches(args[0], subCommands.keySet(), completions);
    } else if (args.length > 1 && subCommands.containsKey(args[0])) {
      List<String> result = subCommands.get(args[0]).onTabComplete(sender, alias, args);
      if (result != null) {
        completions = result;
      }
    }
    return completions;
  }

  public void addSubCommand(String name, CommandSub commandSub) {
    subCommands.put(name, commandSub);
  }

  private void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }
}
