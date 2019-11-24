package dev.tycho.stonks.model.store;

import java.sql.SQLException;

public class SyncStore<T extends Entity> extends Store<T> {

  private DatabaseInterface<T> dbi;
  public SyncStore(DatabaseInterface<T> dbi) {
    this.dbi = dbi;
  }

  @Override
  protected void populate() {
    try {
      for (T e: dbi.loadAll()) {
        entities.put(e.pk, e);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public T get(int pk) {
    if (entities.containsKey(pk)) return entities.get(pk);
    return null;
  }

  @Override
  public void save(T obj) {
    try {
      dbi.save(obj);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void create(T obj) {
    if (obj.pk != 0) {
      throw new IllegalArgumentException("Entity to create already has a primary key");
    }
    int pk = 0;
    try {
      pk = dbi.create(obj);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (entities.containsKey(pk)) {
      throw new IllegalArgumentException("Created new entity but we already have the new pk stored");
    }
    entities.put(pk, obj);
  }
}
