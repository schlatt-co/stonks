package dev.tycho.stonks.command.base.validators;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OnlinePlayerValidator extends ArgumentProvider<Player> {

  public OnlinePlayerValidator(String name) {
    super(name, Player.class);
  }

  @Override
  public Player provideArgument(String arg) {
    return Bukkit.getPlayer(arg);
  }

  @Override
  public String getHelp() {
    return "Must be an online player.";
  }
}
