package dev.tycho.stonks.command.subs.moderator;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RefreshCommandSub extends CommandSub {
  public RefreshCommandSub() {
    super("trevor.mod");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    Repo.getInstance().repopulateAll();
  }
}
