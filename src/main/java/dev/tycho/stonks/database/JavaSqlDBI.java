package dev.tycho.stonks.database;

import java.sql.Connection;

public abstract class JavaSqlDBI<T extends Entity> implements DatabaseInterface<T> {
  protected Connection connection;
  public JavaSqlDBI(Connection connection) {
    this.connection = connection;
  }
  protected abstract boolean createTable();

}
