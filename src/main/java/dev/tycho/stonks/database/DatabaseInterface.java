package dev.tycho.stonks.database;

import java.util.Collection;

public interface DatabaseInterface<T extends Entity> {
  T create(T obj);

  boolean delete(T obj);

  boolean save(T obj);

  T load(int pk);

  Collection<T> loadAll();
}
