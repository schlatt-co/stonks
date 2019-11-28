package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.AccountTypeSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.PlayerStateData;
import dev.tycho.stonks.managers.SettingsManager;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.AccountLink;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.store.Repo;
import dev.tycho.stonks.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class CreateAccountCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " createaccount <account name>");
      return;
    }
    String accountName = concatArgs(1, args);

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
                          if (b) createAccount(player, company,  accountName, type);
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

    Stonks.newChain()
        .async(() -> {
          if (company == null) {
            sendMessage(player, "Invalid company name!");
            return;
          }

          for (AccountLink account : Repo.getInstance().companyAccountLinks().getChildren(company)) {
            if (Repo.getInstance().resolveAccountLink(account).getName().equals(newAccountName)) ;
            sendMessage(player, "Account name already exist in company!");
            return;
          }

          Member member = Repo.getInstance().getCompanyMember(company, player);
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

          Account account;
            switch (accountType) {
                case HoldingsAccount:
                    //Create a new holdings account
                    account = Repo.getInstance().createHoldingsAccount(company, newAccountName, player);
                    break;
                case CompanyAccount:
                    account = Repo.getInstance().createCompanyAccount(company, newAccountName, player);
                    break;
                default:
                    //account type not recognised
                    throw new IllegalArgumentException("AAAAAAAAAAAAAAAAAAAAa");
            }
          sendMessage(player, "Account creation successful!");
          sendMessage(player, "Account ID: " + ChatColor.YELLOW + Repo.getInstance().accountLinkForAccount(account).getPk());

          PlayerStateData.getInstance().setPlayerCreateAccountCooldown(player.getUniqueId(), System.currentTimeMillis());
        }).execute();

  }


}
