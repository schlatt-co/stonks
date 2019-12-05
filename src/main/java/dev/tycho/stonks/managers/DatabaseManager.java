//package dev.tycho.stonks.managers;
//
//import dev.tycho.stonks.Stonks;
//import dev.tycho.stonks.command.MainCommand;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Properties;
//
//public class DatabaseManager extends SpigotModule {
//
//  private static DatabaseManager instance;
//
//  public static DatabaseManager getInstance() {
//    return instance;
//  }
//
//  public DatabaseManager(Stonks plugin) {
//    super("databaseManager", plugin);
//    instance = this;
//    try {
//      this.connection = createConnection();
//    } catch (SQLException e) {
//      e.printStackTrace();
//      connection = null;
//    }
//  }
//
//  private Connection connection;
//
//  public Connection getConnection() {
//    return connection;
//  }
//
//  @Override
//  public void enable() {
//
//  }
//
//  private Connection createConnection() throws SQLException {
//    String host = plugin.getConfig().getString("mysql.host");
//    String port = plugin.getConfig().getString("mysql.port");
//    String database = plugin.getConfig().getString("mysql.database");
//    String username = plugin.getConfig().getString("mysql.username");
//    String password = plugin.getConfig().getString("mysql.password");
//    String useSsl = plugin.getConfig().getString("mysql.ssl");
//    String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
//        "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL="
//        + useSsl;
//
//    Properties connectionProps = new Properties();
//    connectionProps.put("user", username);
//    connectionProps.put("password", password);
//    Connection conn = DriverManager.getConnection(url, connectionProps);
//    System.out.println("Connected to database");
//    return conn;
//  }
//
//
//  @Override
//  public void addCommands() {
//    MainCommand command = new MainCommand();
//    addCommand("company", command);
//    plugin.getCommand("company").setTabCompleter(command);
//  }
//
//  @Override
//  public void disable() {
//  }
//}
