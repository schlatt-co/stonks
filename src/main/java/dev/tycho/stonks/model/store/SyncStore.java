package dev.tycho.stonks.model.store;

import com.google.common.collect.ImmutableList;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Predicate;

public class SyncStore<T extends Entity> implements Store<T> {

  HashMap<Integer, T> entities = new HashMap<>();
  private DatabaseInterface<T> dbi;
  public SyncStore(DatabaseInterface<T> dbi) {
    this.dbi = dbi;
    populate();
  }

  @Override
  public void populate() {
    try {
      for (T e : dbi.loadAll()) {
        entities.put(e.getPk(), e);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
  public T get(int pk) {
    if (entities.containsKey(pk)) return entities.get(pk);
    return null;
  }

  @Override
  public void create(T obj) {
    if (obj.getPk() != 0) {
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
  @Override
  public ImmutableList<T> getAll() {
    return ImmutableList.copyOf(entities.values());
  }

  @Override
  public T getWhere(Predicate<T> p) {
    for (T entity : entities.values()) {
      if (p.test(entity)) return entity;
    }
    return null;
  }
}
