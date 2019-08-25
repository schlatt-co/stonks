package dev.tycho.stonks.managers;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import dev.tycho.stonks.Database.*;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.CommandCompany;
import dev.tycho.stonks.Database.Account;
import dev.tycho.stonks.Database.AccountLink;
import dev.tycho.stonks.Database.AccountLinkDaoImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager extends SpigotModule {

    public DatabaseManager(Stonks plugin) {
        super("databaseManager", plugin);
    }
    private JdbcConnectionSource connectionSource = null;

    private CompanyDao companyDao = null;
    private MemberDao memberDao = null;
    private CompanyAccountDao companyAccountDao = null;
    private AccountLinkDaoImpl accountlinkDao = null;
    private Dao<Holding, Integer> holdingDao = null;
    private Dao<HoldingsAccount, Integer> holdingAccountDao = null;

    @Override
    public void enable() {
        synchronized (this) {
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
                accountlinkDao =  new AccountLinkDaoImpl(connectionSource);
                holdingDao = DaoManager.createDao(connectionSource, Holding.class);
                holdingAccountDao = DaoManager.createDao(connectionSource, HoldingsAccount.class);


                TableUtils.createTableIfNotExists(connectionSource, Company.class);
                TableUtils.createTableIfNotExists(connectionSource, Member.class);
                TableUtils.createTableIfNotExists(connectionSource, AccountLink.class);
                TableUtils.createTableIfNotExists(connectionSource, CompanyAccount.class);
                TableUtils.createTableIfNotExists(connectionSource, Holding.class);
                TableUtils.createTableIfNotExists(connectionSource, HoldingsAccount.class);

//                Subclass1 boi = new Subclass1();
//                boi.setName("big one");
//                boi.setCustomField1("smol");
//                System.out.println("  ____  Created Entity ID:  ____  ");
//                System.out.println(boi.getId());
//                subclass1Dao.create(boi);
//                System.out.println(boi.getId());


            } catch (SQLException e) {
                e.printStackTrace();
            }




        }
    }

    @Override
    public void addCommands() {
        addCommand("company", new CommandCompany(this, plugin));
    }

    @Override
    public void disable() {
        try {
            connectionSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CompanyDao getCompanyDao() {
        return companyDao;
    }

    public MemberDao getMemberDao() {
        return memberDao;
    }

    public AccountLinkDaoImpl getAccountLinkDao() {        return accountlinkDao;    }

    public CompanyAccountDao getCompanyAccountDao() { return companyAccountDao; }
    public Dao<HoldingsAccount, Integer> getHoldingAccountDao() {return  holdingAccountDao;}
    public Dao<Holding, Integer> getHoldingDao() {return  holdingDao;}

    //TODO move this method to a better place
    public Account getAccountWithUUID(UUID uuid) {
        try {
            //Try company account first as those are the most common
            QueryBuilder<CompanyAccount, Integer> queryBuilder = getCompanyAccountDao().queryBuilder();
            queryBuilder.where().eq("uuid", uuid);
            CompanyAccount companyAccount = queryBuilder.queryForFirst();
            //If no company account was found try and find a holdings account
            if (companyAccount == null) {
                QueryBuilder<HoldingsAccount, Integer> queryBuilder2 = getHoldingAccountDao().queryBuilder();
                queryBuilder2.where().eq("uuid", uuid);
                //This will either return a result or null
                //If it is null there are no accounts with this match
                return queryBuilder2.queryForFirst();
            } else {
                return companyAccount;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
