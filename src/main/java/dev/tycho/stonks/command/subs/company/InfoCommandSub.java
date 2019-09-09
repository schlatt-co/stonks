package dev.tycho.stonks.command.subs.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InfoCommandSub extends CommandSub {

  public InfoCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " info <company name>");
      return;
    }
    DatabaseHelper.getInstance().openCompanyInfo(player, concatArgs(1, args));
  }
}
