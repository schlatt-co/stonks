package dev.tycho.stonks.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.table.TableUtils;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.MainCommand;
import dev.tycho.stonks.database.*;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseManager extends SpigotModule {

  private JdbcConnectionSource connectionSource = null;
  private CompanyDao companyDao = null;
  private MemberDao memberDao = null;
  private CompanyAccountDao companyAccountDao = null;
  private AccountLinkDaoImpl accountlinkDao = null;
  private HoldingDaoImpl holdingDao = null;
  private HoldingsAccountDaoImpl holdingAccountDao = null;
  private TransactionDaoImpl transactionDao = null;
  private Dao<Service, Integer> serviceDao = null;
  private SubscriptionDaoImpl subscriptionDao = null;


  public DatabaseManager(Stonks plugin) {
    super("databaseManager", plugin);
  }

  @Override
  public void enable() {
    System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");

    String host = plugin.getConfig().getString("mysql.host");
    String port = plugin.getConfig().getString("mysql.port");
    String database = plugin.getConfig().getString("mysql.database");
    String username = plugin.getConfig().getString("mysql.username");
    String password = plugin.getConfig().getString("mysql.password");
    String useSsl = plugin.getConfig().getString("mysql.ssl");

    String databaseUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL=" + useSsl;

    try {
      connectionSource = new JdbcConnectionSource(databaseUrl, username, password);
      companyDao = DaoManager.createDao(connectionSource, Company.class);
      memberDao = DaoManager.createDao(connectionSource, Member.class);
      companyAccountDao = new CompanyAccountDaoImpl(connectionSource);
      accountlinkDao = new AccountLinkDaoImpl(connectionSource);
      holdingDao = DaoManager.createDao(connectionSource, Holding.class);
      holdingAccountDao = new HoldingsAccountDaoImpl(connectionSource);
      transactionDao = new TransactionDaoImpl(connectionSource);
      subscriptionDao = new SubscriptionDaoImpl(connectionSource);
      serviceDao = DaoManager.createDao(connectionSource, Service.class);


      TableUtils.createTableIfNotExists(connectionSource, Company.class);
      TableUtils.createTableIfNotExists(connectionSource, Member.class);
      TableUtils.createTableIfNotExists(connectionSource, AccountLink.class);
      TableUtils.createTableIfNotExists(connectionSource, CompanyAccount.class);
      TableUtils.createTableIfNotExists(connectionSource, Holding.class);
      TableUtils.createTableIfNotExists(connectionSource, HoldingsAccount.class);
      TableUtils.createTableIfNotExists(connectionSource, Transaction.class);
      TableUtils.createTableIfNotExists(connectionSource, Service.class);
      TableUtils.createTableIfNotExists(connectionSource, Subscription.class);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    new DatabaseHelper(plugin, this);
  }

  @Override
  public void addCommands() {
    MainCommand command = new MainCommand();
    addCommand("company", command);
    plugin.getCommand("company").setTabCompleter(command);
  }

  @Override
  public void disable() {
    try {
      connectionSource.close();
    } catch (IOException ignored) {
    }
  }
}
