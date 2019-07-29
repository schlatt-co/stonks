package nl.tychovi.stonks;

import com.mysql.jdbc.Connection;
import fr.minuskube.inv.InventoryManager;
import nl.tychovi.stonks.command.CommandCompany;
import nl.tychovi.stonks.util.DataStore;
import nl.tychovi.stonks.util.DatabaseConnector;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Stonks extends JavaPlugin {
    private Connection connection;
    private DataStore store;

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this);
        InventoryManager invManager = new InventoryManager(this);
        invManager.init();
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        DatabaseConnector connector = new DatabaseConnector(this);
        store = new DataStore(connector);
        this.getCommand("company").setExecutor(new CommandCompany(store, invManager));
        Bukkit.getOfflinePlayer()
    }

    @Override
    public void onDisable() {
        try { //using a try catch to catch connection errors (like wrong sql password...)
            if (connection != null && !connection.isClosed()) { //checking if connection isn't null to avoid receiving a nullpointer
                connection.close(); //closing the connection field variable.
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
