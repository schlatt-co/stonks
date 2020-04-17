package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.SimpleCommandSub;
import dev.tycho.stonks.gui.CompanyListGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class ListHiddenCommandSub extends SimpleCommandSub {

  public ListHiddenCommandSub() {
    setPermissions("stonks.list.hidden", "stonks.mod");
  }

  @Override
  public void execute(Player player) {
    new CompanyListGui(Repo.getInstance().companies().getAllWhere(c -> c.hidden)).show(player);
  }
}
