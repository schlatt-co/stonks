package dev.tycho.stonks.command.base.validators;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OnlinePlayerValidator extends ArgumentValidator<Player> {

  public OnlinePlayerValidator(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    Player player = Bukkit.getPlayer(str);
    if (player == null) {
      return false;
    }
    value = player;
    return true;
  }

  @Override
  public String getPrompt() {
    return "must be an online player";
  }
}
