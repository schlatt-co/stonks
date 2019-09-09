package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ServicesCommandSub extends CommandSub {
  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2){
      sendMessage(player, "Correct usage /" + alias + " services <account_id>" );
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Correct user: " + ChatColor.YELLOW + "/" + alias + " services <account id>");
      return;
    }

    DatabaseHelper.getInstance().openCompanyServices(player, Integer.parseInt(args[1]));

  }
}
