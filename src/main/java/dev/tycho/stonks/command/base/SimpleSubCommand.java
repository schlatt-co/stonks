package dev.tycho.stonks.command.base;

import org.bukkit.entity.Player;

public abstract class SimpleSubCommand extends SubCommand {
  @Override
  public final void onCommand(Player player, String alias, String[] args) {
    execute(player);
  }

  public abstract void execute(Player player);

}
