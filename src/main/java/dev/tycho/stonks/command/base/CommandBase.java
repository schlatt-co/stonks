package dev.tycho.stonks.command.base;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandBase implements CommandExecutor, TabCompleter {

  private HashMap<String, SubCommand> subCommands;

  public CommandBase() {
    subCommands = new HashMap<>();
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
    return null;
  }

  public static void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }
}
