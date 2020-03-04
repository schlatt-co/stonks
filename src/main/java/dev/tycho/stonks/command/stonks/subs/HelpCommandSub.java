package dev.tycho.stonks.command.stonks.subs;

import dev.tycho.stonks.command.base.SimpleCommandSub;
import org.bukkit.entity.Player;

public class HelpCommandSub extends SimpleCommandSub {

  @Override
  public void execute(Player player) {
    sendMessage(player, "Command Help:");
    sendMessage(player, "To view all commands and more info about the plugin please go to https://stonks.company/");
  }
}
