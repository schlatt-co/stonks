package nl.tychovi.stonks.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.tychovi.stonks.Database.Company;
import nl.tychovi.stonks.Stonks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManagerV2 extends SpigotModule {

    public DatabaseManagerV2(Stonks plugin) {
        super("Database Manager v2", plugin);
    }
    private JdbcConnectionSource connectionSource = null;


    @Override
    public void enable() {
        synchronized (this) {
            String host = plugin.getConfig().getString("mysql.host");
            String port = plugin.getConfig().getString("mysql.port");
            String database = plugin.getConfig().getString("mysql.database");
            String username = plugin.getConfig().getString("mysql.username");
            String password = plugin.getConfig().getString("mysql.password");
            String useSsl = plugin.getConfig().getString("mysql.ssl");

            String databaseUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL=" + useSsl;

            try {
                connectionSource = new JdbcConnectionSource(databaseUrl, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(connectionSource == null) {
                return;
            }


            Dao<Company, UUID> companyDao = null;
            try {
                companyDao = DaoManager.createDao(connectionSource, Company.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                TableUtils.createTableIfNotExists(connectionSource, Company.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Company testCompany = new Company("Longyeet", "Shortyeet");

            try {
                assert companyDao != null;
                companyDao.create(testCompany);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disable() {
        try {
            connectionSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
