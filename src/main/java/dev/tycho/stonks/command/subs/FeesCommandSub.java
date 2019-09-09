package dev.tycho.stonks.command.subs;

import dev.tycho.stonks.command.base.CommandSub;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FeesCommandSub extends CommandSub {

  private final static double COMPANY_FEE = Bukkit.getPluginManager().getPlugin("Stonks").getConfig().getDouble("fees.companycreation");
  private final static double ACCOUNT_FEE = Bukkit.getPluginManager().getPlugin("Stonks").getConfig().getDouble("fees.companyaccountcreation");
  private final static double HOLDING_FEE = Bukkit.getPluginManager().getPlugin("Stonks").getConfig().getDouble("fees.holdingsaccountcreation");

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    sendMessage(player, "Stonks Fees:");
    sendMessage(player, "Company Creation: $" + COMPANY_FEE);
    sendMessage(player, "Company Account Creation: $" + ACCOUNT_FEE);
    sendMessage(player, "Company Holdings Account Creation: $" + HOLDING_FEE);
  }
}
