package nl.tychovi.stonks.util;

import com.mysql.jdbc.Connection;
import io.ebeaninternal.server.lib.Str;
import nl.tychovi.stonks.model.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

    private Connection connection;
    private String username;
    private String password;
    private String url;
    private FileConfiguration config;

    public DatabaseConnector(JavaPlugin plugin) {
        config = plugin.getConfig();

        try { //We use a try catch to avoid errors, hopefully we don't get any.
            Class.forName("com.mysql.jdbc.Driver"); //this accesses Driver in jdbc.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }

        username = config.getString("connection.username");
        password = config.getString("connection.password");
        url = "jdbc:mysql://" + config.getString("connection.host") + ":"
                + config.getString("connection.port")
                + "/" + config.getString("connection.database")
                + "?useSSL=" + config.getString("connection.ssl");

        try { //Another try catch to get any SQL errors (for example connections errors)
            connection = (Connection) DriverManager.getConnection(url, username, password);
            //with the method getConnection() from DriverManager, we're trying to set
            //the connection's url, username, password to the variables we made earlier and
            //trying to get a connection at the same time. JDBC allows us to do this.
        } catch (SQLException e) { //catching errors)
            connection = null;
            e.printStackTrace(); //prints out SQLException errors to the console (if any)
        }

        if (connection == null) {
            System.out.println("Could not connect to DB");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        setupTables();

    }

    private void setupTables() {
        //Add the company table
        noReturnStmt(
                "CREATE TABLE IF NOT EXISTS `company` (" +
                        " `id` INT NOT NULL AUTO_INCREMENT ," +
                        " `name` VARCHAR(64) NOT NULL ," +
                        " `creator_uuid` VARCHAR(36) NOT NULL ," +
                        " `creation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP , " +
                        " PRIMARY KEY (`id`), UNIQUE (`name`))", false);
        //Account table
        noReturnStmt(
                "CREATE TABLE IF NOT EXISTS `account` (" +
                        " `id` INT NOT NULL AUTO_INCREMENT ," +
                        " `fk_company_id` INT NOT NULL ," +
                        " `name` VARCHAR(64) NOT NULL ," +
                        " `creator_uuid` VARCHAR(36) NOT NULL ," +
                        " `creation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP , " +
                        " PRIMARY KEY (`id`), UNIQUE (`name`)," +
                        " KEY `fk_company_id` (`fk_company_id`)," +
                        " CONSTRAINT `account_ibfk_1` FOREIGN KEY (`fk_company_id`) REFERENCES `company` " +
                        "(`id`) ON DELETE CASCADE ON UPDATE CASCADE" +
                        ")", false);
        //Company account table
        noReturnStmt(
                "CREATE TABLE `company_account` (" +
                        " `id` int(11) NOT NULL AUTO_INCREMENT," +
                        " `fk_account_id` int(11) NOT NULL," +
                        " `balance` double NOT NULL DEFAULT '0'," +
                        " PRIMARY KEY (`id`)," +
                        " KEY `fk_account_id` (`fk_account_id`)," +
                        " CONSTRAINT `company_account_ibfk_1` FOREIGN KEY (`fk_account_id`) REFERENCES `account` " +
                        "(`id`) ON DELETE CASCADE ON UPDATE CASCADE" +
                        ")", false);
        //Holdings account table
        noReturnStmt(
                "CREATE TABLE `holdings_account` (" +
                        "  `id` int(11) NOT NULL AUTO_INCREMENT," +
                        "  `fk_account_id` int(11) NOT NULL," +
                        "  PRIMARY KEY (`id`)," +
                        "  KEY `fk_account_id` (`fk_account_id`)," +
                        "  CONSTRAINT `holdings_account_ibfk_1` FOREIGN KEY (`fk_account_id`) REFERENCES `account` " +
                        "(`id`) ON DELETE CASCADE ON UPDATE CASCADE" +
                        ")", false);
        //Holdings table
        noReturnStmt(
                "CREATE TABLE `holding` (" +
                        " `id` int(11) NOT NULL AUTO_INCREMENT," +
                        " `fk_holdings_account_id` int(11) NOT NULL," +
                        " `player_uuid` varchar(36) NOT NULL," +
                        " `share` double NOT NULL," +
                        " `balance` double NOT NULL," +
                        " PRIMARY KEY (`id`)," +
                        " KEY `fk_holdings_account_id` (`fk_holdings_account_id`)," +
                        " CONSTRAINT `holding_ibfk_1` FOREIGN KEY (`fk_holdings_account_id`) REFERENCES `holdings_account` " +
                        "(`id`) ON DELETE CASCADE ON UPDATE CASCADE" +
                        ")", false);
    }


    public Connection getConnection() {
        return connection;
    }

    public void noReturnStmt(String sql, boolean shouldThrow) {
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            // I use executeUpdate() to update the databases table.
            stmt.executeUpdate();
        } catch (SQLException e) {
           if (shouldThrow) e.printStackTrace();
        }
    }

    public boolean createAccount(String name, String creator_uuid) {
        try {
            String sql = "INSERT INTO account(name, creator_uuid) VALUES (?, ?);";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, creator_uuid);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Update the database for changes to existing objects
    private boolean saveCompanyAccount(CompanyAccount ca) {
        String sql = "UPDATE company_account SET balance = ? WHERE id = ?;";
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, String.valueOf(ca.getBalance()));
            stmt.setString(2, String.valueOf(ca.id()));
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
            //First update the account base class
            String sql = "UPDATE holding SET share = ?, balance = ? WHERE id = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
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

    public boolean saveAccount(Account a) {
        try {
            //First update the account base class
            String sql = "UPDATE account SET name = ? WHERE id = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, a.getName());
            stmt.setString(2, String.valueOf(a.id()));
            stmt.executeUpdate();
            //Now update the holding or company respectively
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

    public boolean saveCompany(Company c) {
        try {
            //First update the account base class
            String sql = "UPDATE company SET name = ? WHERE id = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, c.getName());
            stmt.setString(2, String.valueOf(c.id()));
            stmt.executeUpdate();
            c.clean();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Returns the ID of the created company row, otherwise -1
    public int createCompany(String name, String creator_uuid) {
        try {
            String sql =
                    "INSERT INTO company (name, creator_uuid) VALUES (?, ?);";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, creator_uuid);
            return insertStmtGetId(stmt);

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int createHoldingsAccount(Company company, String name, String creator_uuid) {
        int accountBase = createAccountBase(company, name, creator_uuid);
        if (accountBase == -1) return -1;
        try {
            String sql =
                    "INSERT INTO holdings_account (fk_account_id) VALUES (?);";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, accountBase);
            return insertStmtGetId(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public int createCompanyAccount(Company company, String name, String creator_uuid) {
        int accountBase = createAccountBase(company, name, creator_uuid);
        if (accountBase == -1) return -1;
        try {
            String sql =
                    "INSERT INTO company_account (fk_account_id) VALUES (?);";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, accountBase);
            return insertStmtGetId(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int createAccountBase(Company company, String name, String creator_uuid) {
        try {
            String sql =
                    "INSERT INTO account (fk_company_id, name, creator_uuid) VALUES (?, ?, ?);";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, String.valueOf(company.id()));
            stmt.setString(2, name);
            stmt.setString(3, creator_uuid);
            return insertStmtGetId(stmt);

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int insertStmtGetId(PreparedStatement stmt) throws SQLException {
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }

    public ArrayList<String> listCompanies() {
        try {
            ArrayList<String> names = new ArrayList<String>();
            String sql = "SELECT name FROM company";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                names.add(results.getString("name"));
            }
            return names;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    public List<Company> getCompanies() {
        try {
            List<Company> companies = new ArrayList<>();
            String sql = "SELECT * FROM company";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                companies.add(new Company(
                        results.getInt("id"),
                        results.getString("name")
                        ));
            }
            return companies;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Account> getAccountsForCompany(Company c) {
        //todo replace with an inner join
        try {
            //Get all the account bases
            List<Account> accounts = new ArrayList<>();
            ResultSet baseResults;
            { //restrict scope
                String sql = "SELECT * FROM account WHERE fk_company_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, c.id());
                baseResults = stmt.executeQuery();
            }
            //For each account base class, find a subclass
            while (baseResults.next()) {
                int baseId = baseResults.getInt("id");
                System.out.println("Base class ID: " + baseId);
                //First try and find a company account
                ResultSet companyAccountResults;
                {
                    String sql = "SELECT * FROM company_account WHERE fk_account_id = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setInt(1, baseId);
                    companyAccountResults = stmt.executeQuery();
                }
                if (companyAccountResults.next()) {
                    accounts.add(new CompanyAccount(
                            companyAccountResults.getInt("id"),
                            baseResults.getString("name"),
                            companyAccountResults.getDouble("balance")
                    ));
                    System.out.println("^^ this is a company account");
                } else {
                    //Otherwise there is a holdings account subclass
                    ResultSet holdingsAccountResults;
                    {
                        String sql = "SELECT * FROM holdings_account WHERE fk_account_id = ?";
                        PreparedStatement stmt = connection.prepareStatement(sql);
                        stmt.setInt(1, baseId);
                        holdingsAccountResults = stmt.executeQuery();
                    }
                    if (holdingsAccountResults.next()) {
                        System.out.println("^^ this is a holdings account");
                        HoldingsAccount holdingsAccount = new HoldingsAccount(
                                holdingsAccountResults.getInt("id"),
                                baseResults.getString("name")
                        );

                        //Now add all holdings for this holdings account
                        ResultSet holdingResults;
                        {
                            String sql = "SELECT * FROM holding WHERE fk_holdings_account_id = ?";
                            PreparedStatement stmt = connection.prepareStatement(sql);
                            stmt.setInt(1, holdingsAccount.id());
                            holdingResults = stmt.executeQuery();
                        }
                        while (holdingResults.next()) {
                            holdingsAccount.addHolding(new Holding(
                                    holdingResults.getInt("id"),
                                    holdingResults.getString("player_uuid"),
                                    holdingResults.getDouble("share"),
                                    holdingResults.getDouble("balance")
                            ));
                        }

                        accounts.add(holdingsAccount);

                    } else {
                        System.out.println("^^ this is a unknown account");
                        //Big error we have no child class
                    }

                }
            }
            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    void companyFromRow(String row[]) {

    }


}
