package dev.tycho.stonks.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BukkitUser implements StonksUser {

  private OfflinePlayer user;

  public BukkitUser(OfflinePlayer user) {
    this.user = user;
  }

  @Override
  public Player getBase() {
    return null;
  }
}
