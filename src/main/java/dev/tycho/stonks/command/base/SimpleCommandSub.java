package dev.tycho.stonks.command.base;

import org.bukkit.entity.Player;

import java.util.List;

public abstract class SimpleCommandSub extends CommandSub {
  @Override
  public final void onCommand(Player player, String alias, String[] args) {
    execute(player);
  }

  @Override
  public final List<String> getTabCompletions(Player player, String[] args) {
    return null;
  }


  public abstract void execute(Player player);

}
