package dev.tycho.stonks.command.stonks.subs.holding;

import dev.tycho.stonks.command.base.SimpleCommandSub;
import dev.tycho.stonks.gui.AllPlayerHoldingsGui;
import org.bukkit.entity.Player;

public class MyHoldingsCommandSub extends SimpleCommandSub {
  @Override
  public void execute(Player player) {
    new AllPlayerHoldingsGui(player).show(player);
  }
}
