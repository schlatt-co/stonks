package dev.tycho.stonks.command.subs.moderator;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.gui.CompanyListGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class ListHiddenSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    new CompanyListGui(Repo.getInstance().companies().getAllWhere(c -> c.hidden)).show(player);
  }
}
