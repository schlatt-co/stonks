package dev.tycho.stonks.util;

import com.earth2me.essentials.User;
import org.bukkit.entity.Player;

public class EssentialsUser implements StonksUser {

  private User user;

  public EssentialsUser(User user) {
    this.user = user;
  }

  @Override
  public Player getBase() {
    if (user != null) {
      return user.getBase();
    }
    return null;
  }
}
