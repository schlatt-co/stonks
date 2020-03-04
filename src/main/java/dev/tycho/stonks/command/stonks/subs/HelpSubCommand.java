package dev.tycho.stonks.command.stonks.subs;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import org.bukkit.entity.Player;

public class HelpSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    sendMessage(player, "Command Help:");
    sendMessage(player, "To view all commands and more info about the plugin please go to https://stonks.company/");
  }
}
