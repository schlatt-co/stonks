package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountTypeSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.PlayerStateData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.managers.SettingsManager;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CreateAccountCommandSub extends ModularCommandSub {

  public CreateAccountCommandSub() {
    super(new StringValidator("account_name"));
  }

  @Override
  public void execute(Player player) {
    String accountName = getArgument("account_name");
    new AccountTypeSelectorGui.Builder()
        .title("Select an account type")
        .typeSelected(type -> {
              Collection<Company> list;
              //Get all the accounts the player is a manager of
              list = Repo.getInstance().companiesWhereManager(player);
              new CompanySelectorGui.Builder()
                  .title("Select a company")
                  .companies(list)
                  .companySelected(company -> new ConfirmationGui.Builder()
                      .title("Accept creation fee?")
                      .onChoiceMade(b -> {
                        if (b) createAccount(player, company, accountName, type);
                      })
                      .open(player))
                  .open(player);
            }
        ).open(player);
  }

  private void createAccount(Player player, Company company, String newAccountName, AccountTypeSelectorGui.AccountType accountType) {
    if (!player.isOp() && (System.currentTimeMillis() - PlayerStateData.getInstance().getPlayerCreateAccountCooldown(player.getUniqueId())) < SettingsManager.ACCOUNT_CREATION_COOLDOWN) {
      sendMessage(player, "You cannot make an account for another " + Util.convertString(
          SettingsManager.ACCOUNT_CREATION_COOLDOWN - (System.currentTimeMillis() - PlayerStateData.getInstance().getPlayerCreateAccountCooldown(player.getUniqueId()))));
      return;
    }
    if (company == null) {
      sendMessage(player, "Invalid company!");
      return;
    }

    for (Account account : company.accounts) {
      if (account.name.equals(newAccountName)) {
        sendMessage(player, "Account name already exist in company!");
        return;
      }
    }

    Member member = company.getMember(player);
    if (member == null) {
      sendMessage(player, "You are not a member of this company!");
      return;
    }
    if (!member.hasManagamentPermission()) {
      sendMessage(player, "You don't have permission to preform this action. Ask for a promotion!");
      return;
    }
    if (!Stonks.economy.withdrawPlayer(player, SettingsManager.ACCOUNT_FEE).transactionSuccess()) {
      sendMessage(player, "You don't have the sufficient funds for the $" + SettingsManager.ACCOUNT_FEE + " account creation fee.");
      return;
    }

    //Checks done, the account is valid to be created
    Account newAccount;
    switch (accountType) {
      case HoldingsAccount:
        //Create a new holdings account
        newAccount = Repo.getInstance().createHoldingsAccount(company, newAccountName, player);
        break;
      case CompanyAccount:
        newAccount = Repo.getInstance().createCompanyAccount(company, newAccountName, player);
        break;
      default:
        //account type not recognised
        throw new IllegalArgumentException("Account type not recognised. This is your problem");
    }
    if (newAccount == null) {
      sendMessage(player, "Account creation failed! Please tell an admin");
      return;
    }
    sendMessage(player, "Account creation successful!");
    sendMessage(player, "Account ID: " + ChatColor.YELLOW + newAccount.pk);

    PlayerStateData
        .getInstance()
        .setPlayerCreateAccountCooldown(
            player.getUniqueId(),
            System.currentTimeMillis()
        );

  }


}
