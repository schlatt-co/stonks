package dev.tycho.stonks.db_new;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class JavaSqlDBI<T extends Entity> implements DatabaseInterface<T> {
  protected Connection connection;
  public JavaSqlDBI(Connection connection) {
    this.connection = connection;
  }
  protected abstract void createTable() throws SQLException;

}
