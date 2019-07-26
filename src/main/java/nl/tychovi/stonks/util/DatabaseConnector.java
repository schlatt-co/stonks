package nl.tychovi.stonks.util;

import com.mysql.jdbc.Connection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
        url = "jdbc:mysql://" + config.getString("connection.host") + ":" + config.getString("connection.port") + "/" + config.getString("connection.database") + "?useSSL=" + config.getString("connection.ssl");

        try { //Another try catch to get any SQL errors (for example connections errors)
            connection = (Connection) DriverManager.getConnection(url,username,password);
            //with the method getConnection() from DriverManager, we're trying to set
            //the connection's url, username, password to the variables we made earlier and
            //trying to get a connection at the same time. JDBC allows us to do this.
        } catch (SQLException e) { //catching errors)
            connection = null;
            e.printStackTrace(); //prints out SQLException errors to the console (if any)
        }

        if(connection == null) {
            System.out.println("Could not connect to DB");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        noReturnStmt("CREATE TABLE IF NOT EXISTS `company` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(64) NOT NULL , `creator_uuid` VARCHAR(36) NOT NULL , PRIMARY KEY (`id`), UNIQUE (`name`))");

    }

    public Connection getConnection() {
        return connection;
    }

    public void noReturnStmt(String sql) {
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            // I use executeUpdate() to update the databases table.
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Boolean createCompany(String name, String creator_uuid) {
        try {
            String sql = "INSERT INTO company(name, creator_uuid) VALUES (?, ?);";
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

    public ArrayList<String> listCompanies() {
        try {
            ArrayList<String> names = new ArrayList<String>();
            String sql = "SELECT name FROM company";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            while(results.next()) {
                names.add(results.getString("name"));
            }
            return names;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
