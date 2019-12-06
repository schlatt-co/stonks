package dev.tycho.stonks.command.subs.moderator;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanyListGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListHiddenCommandSub extends CommandSub {

  public ListHiddenCommandSub() {
    super("trevor.mod");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    Stonks.newChain()
        .asyncFirst(() ->
            new CompanyListGui(Repo.getInstance().companies().getAllWhere(c -> c.hidden)))
        .sync(gui -> gui.show(player))
        .execute();
  }
}
