package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateCommandSub extends CommandSub {

  private final static double CREATION_FEE = Bukkit.getPluginManager().getPlugin("Stonks").getConfig().getDouble("fees.companycreation");

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct user: " + ChatColor.YELLOW + "/" + alias + " create <company name>");
      return;
    }

    String companyName = concatArgs(1, args);
    new ConfirmationGui.Builder()
        .title("Accept $" + CREATION_FEE + " creation fee?")
        .onChoiceMade(aBoolean -> {
          if (aBoolean) {
            DatabaseHelper.getInstance().createCompany(player, companyName);
          }
        })
        .open(player);
  }
}
