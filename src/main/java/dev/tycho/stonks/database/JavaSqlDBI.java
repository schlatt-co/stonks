package dev.tycho.stonks.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public abstract class JavaSqlDBI<T extends Entity> implements DatabaseInterface<T> {
  protected DataSource dataSource;

  public JavaSqlDBI(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  protected abstract boolean createTable();

  protected Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  protected String uuidToStr(UUID uuid) {
    return uuid == null ? null : uuid.toString();
  }

  public static UUID uuidFromString(String string) {
    if (string == null) {
      return null;
    }
    return UUID.fromString(string);
  }

}
