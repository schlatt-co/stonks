package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    sendMessage(player, "Command Help:");
    sendMessage(player, "To view all commands and more info about the plugin please go to https://stonks.tycho.dev/");
  }
}
