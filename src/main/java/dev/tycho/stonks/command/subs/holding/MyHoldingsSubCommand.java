package dev.tycho.stonks.command.subs.holding;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.gui.AllPlayerHoldingsGui;
import org.bukkit.entity.Player;

public class MyHoldingsSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    new AllPlayerHoldingsGui(player).show(player);
  }
}
