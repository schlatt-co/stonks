package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class RefreshSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    Repo.getInstance().repopulateAll();
  }
}
