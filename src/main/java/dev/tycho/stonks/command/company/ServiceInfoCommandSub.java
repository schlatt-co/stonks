package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.ServiceInfoGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.service.Service;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class ServiceInfoCommandSub extends CommandSub {
  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2) {
      sendMessage(player, ChatColor.RED + "Please specify a service id!");
      return;
    }
    if (!validateDouble(args[1])) {
      sendMessage(player, ChatColor.RED + "Id must be a number");
      return;
    }
    try {
      Service service = DatabaseHelper.getInstance().getDatabaseManager().getServiceDao().queryForId(Integer.parseInt(args[1]));
      if (service == null) {
        player.sendMessage(ChatColor.RED + "Service id not found");
        return;
      }
      ServiceInfoGui.getInventory(service).open(player);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
