package dev.tycho.stonks.db_new;

import com.google.common.collect.ImmutableList;
import dev.tycho.stonks.model.store_old.DatabaseInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SyncStore<T extends Entity> implements Store<T> {

  private HashMap<Integer, T> entities = new HashMap<>();
  private Function<T, T> factory;
  private DatabaseInterface<T> dbi;

  public SyncStore(DatabaseInterface<T> dbi, Function<T, T> factory) {
    this.dbi = dbi;
    this.factory = factory;
    populate();
  }

  @Override
  public void populate() {
    try {
      for (T e : dbi.loadAll()) {
        entities.put(e.pk, e);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void save(T obj) {
    if (!entities.containsKey(obj.pk))
      throw new IllegalArgumentException("Tried to save entity with PK not in entities");
    try {
      dbi.save(obj);
      entities.put(obj.pk, obj);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public T get(int pk) {
    if (entities.containsKey(pk))
      return factory.apply(entities.get(pk));
    return null;
  }

  @Override
  public T create(T obj) {
    if (obj.pk != 0) {
      throw new IllegalArgumentException("Entity to create already has a primary key");
    }
    T created;
    try {
      created = dbi.create(obj);
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
    if (entities.containsKey(created.pk)) {
      throw new IllegalArgumentException("Created new entity but we already have the new pk stored");
    }
    entities.put(created.pk, created);
    return factory.apply(created);
  }

  @Override
  public boolean delete(int pk) {
    if (entities.containsKey(pk)) {
      try {
        if (dbi.delete(entities.get(pk))) {
          entities.remove(pk);
          return true;
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  @Override
  public void refresh(int pk) {
    if (entities.containsKey(pk)) {
      try {
        entities.put(pk, dbi.load(pk));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public ImmutableList<T> getAll() {
    return ImmutableList.copyOf(entities.values());
  }

  @Override
  public T getWhere(Predicate<T> p) {
    for (T entity : entities.values()) {
      if (p.test(entity)) return factory.apply(entity);
    }
    return null;
  }

  @Override
  public ImmutableList<T> getAllWhere(Predicate<T> p) {
    List<T> matches = new ArrayList<>();
    for (T entity : entities.values()) {
      if (p.test(entity)) {
        matches.add(factory.apply(entity));
      }
    }
    return ImmutableList.copyOf(matches);
  }
}
