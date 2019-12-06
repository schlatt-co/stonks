package dev.tycho.stonks.database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

public class DummyDBI<T extends Entity> extends JavaSqlDBI<T> {
  private int index = 0;

  public DummyDBI(Connection connection) {
    super(connection);
  }

  @Override
  public T create(T obj) {
    index++;
    obj.pk = index;
    return obj;
  }

  @Override
  public boolean delete(T obj) {
    return true;
  }

  @Override
  public boolean save(T obj) {
    //pass
    return true;
  }

  @Override
  public T load(int pk) {
    return null;
  }

  @Override
  public Collection<T> loadAll() {
    return new ArrayList<>();
  }

  @Override
  protected boolean createTable() {
    return true;
  }
}
