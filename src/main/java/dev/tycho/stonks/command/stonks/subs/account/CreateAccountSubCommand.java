package dev.tycho.stonks.command.stonks.subs.account;

import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.autocompleters.OptionListAutocompleter;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountTypeSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.PlayerData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.managers.SettingsManager;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CreateAccountSubCommand extends ModularSubCommand {

  public CreateAccountSubCommand() {
    super(new StringValidator("account_name"));
    addAutocompleter("account_name", new OptionListAutocompleter("sales", "shop", "profits"));
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
                  .companySelected(company -> createAccount(player, company, accountName, type))
                  .show(player);
            }
        ).show(player);
  }

  private void createAccount(Player player, Company company, String newAccountName, AccountTypeSelectorGui.AccountType accountType) {
    if (!player.isOp() && (System.currentTimeMillis() - PlayerData.getInstance().getPlayerCreateAccountCooldown(player.getUniqueId())) < SettingsManager.ACCOUNT_CREATION_COOLDOWN) {
      sendMessage(player, "You cannot make an account for another " + Util.convertString(
          SettingsManager.ACCOUNT_CREATION_COOLDOWN - (System.currentTimeMillis() - PlayerData.getInstance().getPlayerCreateAccountCooldown(player.getUniqueId()))));
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

    //Checks done, the account is valid to be created
    Account newAccount;
    switch (accountType) {
      case HoldingsAccount:
        //Create a new holdings account
        newAccount = Repo.getInstance().createHoldingsAccount(company, newAccountName, player);
        break;
      case CompanyAccount:
        newAccount = Repo.getInstance().createCompanyAccount(company, newAccountName);
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

    PlayerData.getInstance().setPlayerCreateAccountCooldown(player.getUniqueId(), System.currentTimeMillis());
  }


}
