package dev.tycho.stonks.gui;

import dev.tycho.stonks.Stonks;
import org.bukkit.entity.Player;

public abstract class AsyncGuiBuilder {
  protected abstract Showable create();
  public void show(Player player) {
    Stonks.newChain()
        .asyncFirst(this::create)
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
