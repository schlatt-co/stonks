package dev.tycho.stonks.command.subs.company;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.PlayerStateData;
import dev.tycho.stonks.managers.SettingsManager;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import org.apache.commons.lang.StringUtils;
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
            createCompany(player, companyName);
          }
        })
        .open(player);
  }


  private void createCompany(Player player, String companyName) {
    //Prevent the player from spamming companies
    if (!player.isOp() && (System.currentTimeMillis() - PlayerStateData.getInstance().getPlayerCreateCompanyCooldown(player.getUniqueId())) < SettingsManager.COMPANY_CREATION_COOLDOWN) {
      sendMessage(player, "You cannot make a company for another " + Util.convertString(SettingsManager.COMPANY_CREATION_COOLDOWN - (System.currentTimeMillis() - PlayerStateData.getInstance().getPlayerCreateCompanyCooldown(player.getUniqueId()))));
      return;
    }
    Stonks.newChain()
        .async(() -> {

          String name = companyName.trim();

          if (!name.matches("[0-9a-zA-Z\\s&+]{2,32}")) {
            sendMessage(player, "Invalid name. Please try again. You may have used special characters or it is too long");
            return;
          }
          if (StringUtils.isNumeric(name)) {
            sendMessage(player, "A company name cannot be a number!");
            return;
          }
          if (Repo.getInstance().companyWithName(name) != null) {
            sendMessage(player, "A company with that name already exists!");
            return;
          }
          double creationFee = SettingsManager.COMPANY_FEE;
          if (!Stonks.economy.withdrawPlayer(player, creationFee).transactionSuccess()) {
            sendMessage(player, "You don't have the sufficient funds for the $" + creationFee + " company creation fee.");
            return;
          }

          Company c = Repo.getInstance().createCompany(name, player);
          if (c != null) {
            sendMessage(player, "Company created successfully!");
          } else {
            sendMessage(player, "Company creation failed");
          }
        }).execute();
  }
}
