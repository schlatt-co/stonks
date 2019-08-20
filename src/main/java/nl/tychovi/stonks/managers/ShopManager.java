package nl.tychovi.stonks.managers;

import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.*;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.j256.ormlite.stmt.QueryBuilder;
import nl.tychovi.stonks.Database.CompanyAccount;
import nl.tychovi.stonks.Database.Member;
import nl.tychovi.stonks.Database.Role;
import nl.tychovi.stonks.Stonks;
import org.bukkit.Bukkit;
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
      if(accountLine.substring(1, indexOfDash).matches("\\d+]")) {
        accountId = Integer.parseInt(accountLine.substring(1, indexOfDash));
      }
    } else {
      return;
    }

    try {
      if(databaseManager.getCompanyAccountDao().idExists(accountId)) {
        CompanyAccount companyAccount = databaseManager.getCompanyAccountDao().queryForId(accountId);
        UUID accountUuid = companyAccount.getCompany().getId();

        QueryBuilder<Member, UUID> queryBuilder = databaseManager.getMemberDao().queryBuilder();
        queryBuilder.where().eq("uuid", event.getPlayer().getUniqueId()).and().eq("company_id", accountUuid);
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
      if(databaseManager.getCompanyAccountDao().idExists(accountId)) {
        CompanyAccount companyAccount = databaseManager.getCompanyAccountDao().queryForId(accountId);
        UUID accountUuid = companyAccount.getUuid();
        Account CSaccount = NameManager.getAccount(accountUuid);

        if(CSaccount == null) {
          String name = companyAccount.getCompany().getName();
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
    int companyAccountId = Integer.parseInt(event.getName().substring(1, dashIndex));
    try {
      CompanyAccount companyAccount = databaseManager.getCompanyAccountDao().queryForId(companyAccountId);
      QueryBuilder<Member, UUID> queryBuilder = databaseManager.getMemberDao().queryBuilder();
      queryBuilder.where().eq("uuid", event.getPlayer().getUniqueId()).and().eq("company_id", companyAccount.getCompany().getId());
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
    try {
      QueryBuilder<CompanyAccount, Integer> queryBuilder = databaseManager.getCompanyAccountDao().queryBuilder();
      queryBuilder.where().eq("uuid", event.getTarget());
      List<CompanyAccount> companyAccounts = null;
      companyAccounts = queryBuilder.query();
      if(!companyAccounts.isEmpty()) {
        event.setCancelled(true);
        companyAccounts.get(0).addBalance(event.getAmountSent().doubleValue());
        databaseManager.getCompanyAccountDao().update(companyAccounts.get(0));
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
