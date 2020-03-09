package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.SimpleCommandSub;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class RefreshCommandSub extends SimpleCommandSub {

  @Override
  public void execute(Player player) {
    Repo.getInstance().repopulateAll();
  }
}
