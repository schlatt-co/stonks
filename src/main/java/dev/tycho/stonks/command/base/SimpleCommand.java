package dev.tycho.stonks.command.base;

import org.bukkit.entity.Player;

public class SimpleCommand extends SubCommand {

  private final SimpleCommandExecutor executor;

  public SimpleCommand(SimpleCommandExecutor executor) {
    this.executor = executor;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    executor.execute(player);
  }

  public interface SimpleCommandExecutor {
    void execute(Player player);
  }

}
