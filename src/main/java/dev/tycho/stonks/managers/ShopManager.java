package dev.tycho.stonks.managers;

import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.*;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.stonks.Database.AccountLink;
import dev.tycho.stonks.Database.CompanyAccount;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.Database.Member;
import dev.tycho.stonks.Database.Role;
import org.bukkit.event.EventHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;


public class ShopManager extends SpigotModule {

  private DatabaseManager databaseManager;

  public ShopManager(Stonks plugin) {
    super("Shop Manager", plugin);
    databaseManager = (DatabaseManager) plugin.getModule("databaseManager");
  }

  @EventHandler
  public void onPreShopCreation(PreShopCreationEvent event) {
    String accountLine = event.getSignLine(NAME_LINE);
    if(!accountLine.startsWith("#")) {
      return;
    }

    int accountId = 0;
    if(accountLine.substring(1).matches("\\d+")) {
      accountId = Integer.parseInt(accountLine.substring(1));
    } else if(accountLine.contains("-")) {
      int indexOfDash = accountLine.indexOf("-");
      if(accountLine.substring(1, indexOfDash).matches("\\d+")) {
        accountId = Integer.parseInt(accountLine.substring(1, indexOfDash));
      }
    } else {
      return;
    }

    try {
      //If an account exists for this account id
      if(databaseManager.getAccountlinkDao().idExists(accountId)) {
        AccountLink link = databaseManager.getAccountlinkDao().queryForId(accountId);
        UUID companyUuid = link.getCompany().getId();

        QueryBuilder<Member, UUID> queryBuilder = databaseManager.getMemberDao().queryBuilder();
        queryBuilder.where().eq("uuid", event.getPlayer().getUniqueId()).and().eq("company_id", companyUuid);
        List<Member> list = queryBuilder.query();
        if(list.isEmpty() || list.get(0).getRole().equals(Role.Slave) || !list.get(0).getAcceptedInvite()) {
          event.setOutcome(PreShopCreationEvent.CreationOutcome.NO_PERMISSION);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onAccountQuery(AccountQueryEvent event) {
    String accountLine = event.getName();
    if(!accountLine.startsWith("#")) {
      return;
    }
    int accountId = 0;
    if(accountLine.substring(1).matches("\\d+")) {
      accountId = Integer.parseInt(accountLine.substring(1));
    } else if(accountLine.contains("-")) {
      int indexOfDash = accountLine.indexOf("-");
      accountId = Integer.parseInt(accountLine.substring(1, indexOfDash));
    } else {
      return;
    }

    try {
      //If an account exists with this id
      if(databaseManager.getAccountlinkDao().idExists(accountId)) {
        AccountLink link = databaseManager.getAccountlinkDao().queryForId(accountId);
        UUID accountUuid = link.getAccount().getUuid();
        //Get the chest shop account associated with this uuid
        Account CSaccount = NameManager.getAccount(accountUuid);

        if(CSaccount == null) {
          //If none exists then make a new one
          String name = link.getCompany().getName();
          Account newCSaccount = new Account("#" + accountId + "-" + name, accountUuid);
          event.setAccount(newCSaccount);
        } else {
          event.setAccount(CSaccount);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onAccountCheck(AccountCheckEvent event) {

  }

  @EventHandler
  public void onOwnEvent(AccountOwnerCheckEvent event) {
    if(!event.getName().startsWith("#")) {
      return;
    }
    int dashIndex = event.getName().indexOf("-");
    if(dashIndex == -1) {
      event.setCancelled(true);
      return;
    }
    int accountId = Integer.parseInt(event.getName().substring(1, dashIndex));
    try {
      //Find the account for this id
      AccountLink link = databaseManager.getAccountlinkDao().queryForId(accountId);
      QueryBuilder<Member, UUID> queryBuilder = databaseManager.getMemberDao().queryBuilder();
      queryBuilder.where().eq("uuid", event.getPlayer().getUniqueId()).and().eq("company_id", link.getCompany().getId());
      List<Member> list = queryBuilder.query();
      if(list.isEmpty() || list.get(0).getRole().equals(Role.Slave) || !list.get(0).getAcceptedInvite()) {
        return;
      }
      event.setCancelled(true);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onEconomyCheck(AccountCheckEvent event) {
    //Try to see if we have an account for this UUID
    //TODO add a second check for other accounts
    try {
      QueryBuilder<CompanyAccount, Integer> queryBuilder = databaseManager.getCompanyAccountDao().queryBuilder();
      queryBuilder.where().eq("uuid", event.getAccount());
      List<CompanyAccount> companyAccounts = null;
      companyAccounts = queryBuilder.query();
      if(!companyAccounts.isEmpty()) {
        event.hasAccount(true);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onCurrencyAdd(PreCurrencyAddEvent event) {
    //todo add a second check here
    try {
      QueryBuilder<CompanyAccount, Integer> queryBuilder = databaseManager.getCompanyAccountDao().queryBuilder();
      queryBuilder.where().eq("uuid", event.getTarget());
      CompanyAccount companyAccount = queryBuilder.queryForFirst();
      if(companyAccount != null) {
        event.setCancelled(true);
        companyAccount.addBalance(event.getAmountSent().doubleValue());
        databaseManager.getCompanyAccountDao().update(companyAccount);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onCurrencySubtract(PreCurrencySubtractEvent event) {
    try {
      QueryBuilder<CompanyAccount, Integer> queryBuilder = databaseManager.getCompanyAccountDao().queryBuilder();
      queryBuilder.where().eq("uuid", event.getSender());
      List<CompanyAccount> companyAccounts = null;
      companyAccounts = queryBuilder.query();
      if(!companyAccounts.isEmpty()) {
        if(!companyAccounts.get(0).subtractBalance(event.getAmountSent().doubleValue())) {
          event.setBalanceSufficient(false);
        }
        event.setCancelled(true);
        databaseManager.getCompanyAccountDao().update(companyAccounts.get(0));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onAmountCheck(PreAmountCheckEvent event) {
    try {
      QueryBuilder<CompanyAccount, Integer> queryBuilder = databaseManager.getCompanyAccountDao().queryBuilder();
      queryBuilder.where().eq("uuid", event.getAccount());
      List<CompanyAccount> companyAccounts = null;
      companyAccounts = queryBuilder.query();
      if(!companyAccounts.isEmpty()) {
        event.setCancelled(true);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onCurrencyCheck(PreCurrencyCheckEvent event) {
    try {
      QueryBuilder<CompanyAccount, Integer> queryBuilder = databaseManager.getCompanyAccountDao().queryBuilder();
      queryBuilder.where().eq("uuid", event.getAccount());
      List<CompanyAccount> companyAccounts = null;
      companyAccounts = queryBuilder.query();
      if(!companyAccounts.isEmpty()) {
        if(companyAccounts.get(0).getBalance() >= event.getAmountSent().doubleValue()) {
          event.setHasEnough(true);
        }
        event.setCancelled(true);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
