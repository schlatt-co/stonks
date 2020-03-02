package dev.tycho.stonks.command.subs;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FeesSubCommand extends SimpleSubCommand {

  private final static double COMPANY_FEE = Bukkit.getPluginManager().getPlugin("Stonks").getConfig().getDouble("fees.companycreation");
  private final static double ACCOUNT_FEE = Bukkit.getPluginManager().getPlugin("Stonks").getConfig().getDouble("fees.companyaccountcreation");
  private final static double HOLDING_FEE = Bukkit.getPluginManager().getPlugin("Stonks").getConfig().getDouble("fees.holdingsaccountcreation");

  @Override
  public void execute(Player player) {
    sendMessage(player, "Stonks Fees:");
    sendMessage(player, "Company Creation: $" + COMPANY_FEE);
    sendMessage(player, "Company Account Creation: $" + ACCOUNT_FEE);
    sendMessage(player, "Company Holdings Account Creation: $" + HOLDING_FEE);
  }
}
