package dev.tycho.stonks.command.base;

import org.bukkit.entity.Player;

public class LambdaSubCommand extends SubCommand {

  private final LambdaCommandExecutor executor;

  public LambdaSubCommand(LambdaCommandExecutor executor) {
    this.executor = executor;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    executor.execute(player);
  }

  public interface LambdaCommandExecutor {
    void execute(Player player);
  }

}
