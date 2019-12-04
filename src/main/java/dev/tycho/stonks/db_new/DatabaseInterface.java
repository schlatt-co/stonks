package dev.tycho.stonks.db_new;

import dev.tycho.stonks.db_new.Entity;

import java.sql.SQLException;
import java.util.Collection;

public interface DatabaseInterface<T extends Entity> {
  T create(T obj) throws SQLException;

  boolean delete(T obj) throws SQLException;

  void save(T obj) throws SQLException;

  T load(int pk) throws SQLException;

  Collection<T> loadAll() throws SQLException;
}
