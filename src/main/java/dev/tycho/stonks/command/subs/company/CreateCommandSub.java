package dev.tycho.stonks.command.subs.company;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.PlayerStateData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.managers.SettingsManager;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CreateCommandSub extends ModularCommandSub {

  private final static double CREATION_FEE = Bukkit.getPluginManager().getPlugin("Stonks").getConfig().getDouble("fees.companycreation");

  public CreateCommandSub() {
    super(ArgumentValidator.concatIfLast(new StringValidator("company_name")));
  }


  @Override
  public void execute(Player player) {
    String companyName = getArgument("company_name");
    new ConfirmationGui.Builder()
        .title("Accept $" + CREATION_FEE + " creation fee?")
        .yes(() ->
            createCompany(player, companyName)).show(player);
  }


  private void createCompany(Player player, String companyName) {
    //Prevent the player from spamming companies
    if (!player.isOp() && (System.currentTimeMillis() - PlayerStateData.getInstance().getPlayerCreateCompanyCooldown(player.getUniqueId())) < SettingsManager.COMPANY_CREATION_COOLDOWN) {
      sendMessage(player, "You cannot make a company for another " + Util.convertString(SettingsManager.COMPANY_CREATION_COOLDOWN - (System.currentTimeMillis() - PlayerStateData.getInstance().getPlayerCreateCompanyCooldown(player.getUniqueId()))));
      return;
    }
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
      Bukkit.broadcastMessage(ChatColor.GREEN + player.getDisplayName() + ChatColor.GREEN + " just founded a new company - " + c.name + "!");
    } else {
      sendMessage(player, "Company creation failed");
    }
  }
}
