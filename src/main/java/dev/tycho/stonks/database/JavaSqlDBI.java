package dev.tycho.stonks.database;

import java.sql.Connection;
import java.util.UUID;

public abstract class JavaSqlDBI<T extends Entity> implements DatabaseInterface<T> {
  protected Connection connection;
  public JavaSqlDBI(Connection connection) {
    this.connection = connection;
  }
  protected abstract boolean createTable();
  protected String uuidToStr(UUID uuid) {
    return uuid == null ? null : uuid.toString();
  }
  protected UUID uuidFromString(String string) {
    if (string == null) {
      return null;
    }
    return UUID.fromString(string);
  }

}
