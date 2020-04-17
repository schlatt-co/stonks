package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.SimpleCommandSub;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class RefreshCommandSub extends SimpleCommandSub {

  public RefreshCommandSub() {
    setPermissions("stonks.admin.refresh", "stonks.admin");
  }

  @Override
  public void execute(Player player) {
    Repo.getInstance().repopulateAll();
  }
}
