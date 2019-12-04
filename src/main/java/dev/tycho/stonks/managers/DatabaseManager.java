package dev.tycho.stonks.managers;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.MainCommand;

import java.io.IOException;

public class DatabaseManager extends SpigotModule {

  private JdbcConnectionSource connectionSource = null;


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
