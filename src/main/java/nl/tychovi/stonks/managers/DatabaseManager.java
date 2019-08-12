package nl.tychovi.stonks.managers;

import fr.minuskube.inv.InventoryManager;
import nl.tychovi.stonks.Stonks;
import nl.tychovi.stonks.command.CommandCompany;
import nl.tychovi.stonks.model.*;
import nl.tychovi.stonks.util.Constants;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class DatabaseManager extends SpigotModule {

  private Connection connection;
  private InventoryManager invManager;

  private List<Entity> entities = new ArrayList<>();
  private List<Company> companies = new ArrayList<>();

  public DatabaseManager(Stonks plugin) {
    super("Database Manager", plugin);
  }

  @Override
  public void enable() {
    synchronized (this) {
      invManager = new InventoryManager(plugin);
      invManager.init();
      try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + ":" + plugin.getConfig().getString("mysql.port") + "/" + plugin.getConfig().getString("mysql.database") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL=" + plugin.getConfig().getString("mysql.ssl"),
            plugin.getConfig().getString("mysql.username"),
            plugin.getConfig().getString("mysql.password"));
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `company` (`id` VARCHAR(36) NOT NULL , `name` VARCHAR(64) NOT NULL , `creator_uuid` VARCHAR(36) NOT NULL , `creation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP , UNIQUE (`id`), UNIQUE (`name`))");
//        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `account` (`id` INT NOT NULL AUTO_INCREMENT , `fk_company_id` INT NOT NULL , `name` VARCHAR(64) NOT NULL , `creator_uuid` VARCHAR(36) NOT NULL , `creation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`id`), UNIQUE (`name`), KEY `fk_company_id` (`fk_company_id`), CONSTRAINT `account_ibfk_1` FOREIGN KEY (`fk_company_id`) REFERENCES `company` (`id`) ON DELETE CASCADE ON UPDATE CASCADE)");
//        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `company_account` (`id` int(11) NOT NULL AUTO_INCREMENT, `fk_account_id` int(11) NOT NULL, `balance` double NOT NULL DEFAULT '0', PRIMARY KEY (`id`), KEY `fk_account_id` (`fk_account_id`), CONSTRAINT `company_account_ibfk_1` FOREIGN KEY (`fk_account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE)");
//        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `holdings_account` (`id` int(11) NOT NULL AUTO_INCREMENT, `fk_account_id` int(11) NOT NULL, PRIMARY KEY (`id`), KEY `fk_account_id` (`fk_account_id`), CONSTRAINT `holdings_account_ibfk_1` FOREIGN KEY (`fk_account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE)");
//        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `holding` (`id` int(11) NOT NULL AUTO_INCREMENT, `fk_holdings_account_id` int(11) NOT NULL, `player_uuid` varchar(36) NOT NULL, `share` double NOT NULL, `balance` double NOT NULL, PRIMARY KEY (`id`), KEY `fk_holdings_account_id` (`fk_holdings_account_id`), CONSTRAINT `holding_ibfk_1` FOREIGN KEY (`fk_holdings_account_id`) REFERENCES `holdings_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE)");
      } catch (ClassNotFoundException | SQLException e) {
        log("Error while connecting to MySQL: " + e.getMessage());
        e.printStackTrace();
        plugin.getServer().getPluginManager().disablePlugin(plugin);
      }
    }
  }

  @Override
  public void addCommands() {
    addCommand("company", new CommandCompany(this, invManager));
  }

  @Override
  public void disable() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException ignored) {
    }
  }

  public void loadModel() {
    companies.addAll(Objects.requireNonNull(getCompanies()));
    for (Company c : companies) {
      for (Account a : Objects.requireNonNull(getAccountsForCompany(c))) {
        c.addAccount(a);
      }
    }
  }

  public void saveModel() {
    for (Company c : companies) {
      for (Account a : c.getAccounts()) {
        saveAccount(a);
      }
      saveCompany(c);
    }
  }

  public boolean createCompanyAccountObject(Company c, String name, String creator_uuid) {
    int id = createCompanyAccount(c, name, creator_uuid);
    if (id == -1) return false;
    CompanyAccount newAccount = new CompanyAccount(id, name, 0);
    c.addAccount(newAccount);
    return true;
  }

  public boolean createCompanyObject(String name, String creator_uuid) {
    UUID uuid = createCompany(name, creator_uuid);
    if (uuid == null) return false;
    Company newCompany = new Company(uuid, name);
    companies.add(newCompany);
    return true;
  }

  public Company getCompanyByName(String name) {
    for (Company c : companies) {
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  public List<Company> getCachedCompanies() {
    return companies;
  }

  public boolean createAccount(String name, String creator_uuid) {
    try {
      PreparedStatement stmt = connection.prepareStatement("INSERT INTO account(name, creator_uuid) VALUES (?, ?);");
      stmt.setString(1, name);
      stmt.setString(2, creator_uuid);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean saveCompanyAccount(CompanyAccount ca) {
    try {
      PreparedStatement stmt = connection.prepareStatement("UPDATE company_account SET balance = ? WHERE id = ?;");
      stmt.setDouble(1, ca.getBalance());
      stmt.setInt(2, ca.id());
      stmt.executeUpdate();
      ca.clean();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean saveHoldingsAccount(HoldingsAccount ha) {
    for (Holding h : ha.getHoldings()) {
      if (!saveHolding(h)) {
        return false;
      }
    }
    ha.clean();
    return true;
  }

  private boolean saveHolding(Holding h) {
    try {
      PreparedStatement stmt = connection.prepareStatement("UPDATE holding SET share = ?, balance = ? WHERE id = ?;");
      stmt.setString(1, String.valueOf(h.getShare()));
      stmt.setString(2, String.valueOf(h.getBalance()));
      stmt.setString(3, String.valueOf(h.id()));
      stmt.executeUpdate();
      h.clean();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean saveAccount(Account a) {
    try {
      PreparedStatement stmt = connection.prepareStatement("UPDATE account SET name = ? WHERE id = ?;");
      stmt.setString(1, a.getName());
      stmt.setInt(2, a.id());
      stmt.executeUpdate();

      IAccountVisitor visitor = new IAccountVisitor() {
        boolean result;

        @Override
        public void visit(HoldingsAccount a) {
          result = saveHoldingsAccount(a);
        }

        @Override
        public void visit(CompanyAccount a) {
          result = saveCompanyAccount(a);
        }

        @Override
        public Object result() {
          return result;
        }
      };
      a.accept(visitor);
      return (boolean) visitor.result();

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean saveCompany(Company c) {
    try {
      PreparedStatement stmt = connection.prepareStatement("UPDATE company SET name = ? WHERE id = ?;");
      stmt.setString(1, c.getName());
      stmt.setString(2, c.id().toString());
      stmt.executeUpdate();
      c.clean();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private UUID createCompany(String name, String creator_uuid) {
    try {
      UUID newUuid = UUID.randomUUID();
      PreparedStatement stmt = connection.prepareStatement("INSERT INTO company (id, name, creator_uuid) VALUES (?, ?, ?);");
      stmt.setString(1, newUuid.toString());
      stmt.setString(2, name);
      stmt.setString(3, creator_uuid);
      stmt.execute();
      return newUuid;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public int createHoldingsAccount(Company company, String name, String creator_uuid) {
    int accountId = createAccountBase(company, name, creator_uuid);
    if (accountId == -1) return -1;
    try {
      PreparedStatement stmt = connection.prepareStatement("INSERT INTO holdings_account (fk_account_id) VALUES (?);", Statement.RETURN_GENERATED_KEYS);
      stmt.setInt(1, accountId);
      stmt.execute();
      ResultSet set = stmt.getGeneratedKeys();
      int id = -1;
      if (set.next()) {
        id = set.getInt(1);
      }
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  private int createCompanyAccount(Company company, String name, String creator_uuid) {
    int accountId = createAccountBase(company, name, creator_uuid);
    if (accountId == -1) return -1;
    try {
      PreparedStatement stmt = connection.prepareStatement("INSERT INTO company_account (fk_account_id) VALUES (?);", Statement.RETURN_GENERATED_KEYS);
      stmt.setInt(1, accountId);
      stmt.execute();
      ResultSet set = stmt.getGeneratedKeys();
      int id = -1;
      if (set.next()) {
        id = set.getInt(1);
      }
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  private int createAccountBase(Company company, String name, String creator_uuid) {
    try {
      PreparedStatement stmt = connection.prepareStatement("INSERT INTO account (fk_company_id, name, creator_uuid) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, company.id().toString());
      stmt.setString(2, name);
      stmt.setString(3, creator_uuid);
      ResultSet set = stmt.getGeneratedKeys();
      int id = -1;
      if (set.next()) {
        id = set.getInt(1);
      }
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public List<String> listCompanies() {
    try {
      List<String> names = new ArrayList<>();
      ResultSet set = connection.createStatement().executeQuery("SELECT name FROM company");
      while (set.next()) {
        names.add(set.getString("name"));
      }
      return names;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  private List<Company> getCompanies() {
    try {
      List<Company> companies = new ArrayList<>();
      ResultSet set = connection.createStatement().executeQuery("SELECT * FROM company");
      while (set.next()) {
        companies.add(new Company(
            UUID.fromString(set.getString("uuid")),
            set.getString("name")
        ));
      }
      return companies;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  private List<Account> getAccountsForCompany(Company c) {
    try {
      List<Account> accounts = new ArrayList<>();
      PreparedStatement stmt = connection.prepareStatement("SELECT * FROM account WHERE fk_company_id = ?");
      stmt.setString(1, c.id().toString());
      ResultSet baseResults = stmt.executeQuery();
      while (baseResults.next()) {
        int baseId = baseResults.getInt("id");
        stmt = connection.prepareStatement("SELECT * FROM company_account WHERE fk_account_id = ?");
        stmt.setInt(1, baseId);
        ResultSet companyAccountResults = stmt.executeQuery();
        if (companyAccountResults.next()) {
          accounts.add(new CompanyAccount(
              companyAccountResults.getInt("id"),
              baseResults.getString("name"),
              companyAccountResults.getDouble("balance")
          ));
        } else {
          stmt = connection.prepareStatement("SELECT * FROM holdings_account WHERE fk_account_id = ?");
          stmt.setInt(1, baseId);
          ResultSet holdingsAccountResults = stmt.executeQuery();
          if (holdingsAccountResults.next()) {
            HoldingsAccount holdingsAccount = new HoldingsAccount(
                holdingsAccountResults.getInt("id"),
                baseResults.getString("name")
            );
            stmt = connection.prepareStatement("SELECT * FROM holding WHERE fk_holdings_account_id = ?");
            stmt.setInt(1, holdingsAccount.id());
            ResultSet holdingResults = stmt.executeQuery();
            while (holdingResults.next()) {
              holdingsAccount.addHolding(new Holding(
                  holdingResults.getInt("id"),
                  UUID.fromString(holdingResults.getString("player_uuid")),
                  holdingResults.getDouble("share"),
                  holdingResults.getDouble("balance")
              ));
            }
            accounts.add(holdingsAccount);
          } else {
            log("Unable to find child for account!");
          }
        }
      }
      return accounts;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
