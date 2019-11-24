package dev.tycho.stonks.model.store;

import java.sql.SQLException;
import java.util.Collection;

public interface DatabaseInterface<T extends Entity> {
  int create(T obj) throws SQLException;

  void save(T obj) throws SQLException;

  T load(int pk) throws SQLException;

  Collection<T> loadAll() throws SQLException;
}
