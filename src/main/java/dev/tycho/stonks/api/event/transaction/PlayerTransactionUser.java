package dev.tycho.stonks.api.event.transaction;

import org.bukkit.entity.Player;

public class PlayerTransactionUser implements ITransactionUser {
  public final Player player;

  public PlayerTransactionUser(Player player) {
    this.player = player;
  }

  @Override
  public String userDisplayName() {
    return player.getName();
  }
}
