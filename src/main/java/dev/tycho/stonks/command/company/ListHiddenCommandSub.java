package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
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
    //todo logic
  }
}
