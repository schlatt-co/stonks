package dev.tycho.stonks.database;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SyncStore<T extends Entity> implements Store<T> {

  private HashMap<Integer, T> entities = new HashMap<>();
  private Function<T, T> factory;
  private JavaSqlDBI<T> dbi;

  public void setDbi(JavaSqlDBI<T> dbi) {
    this.dbi = dbi;
    if (dbi.createTable()) System.out.println("Created table for " + dbi.getClass().getName().replace("DBI", ""));
  }

  public SyncStore(Function<T, T> factory) {
    this.factory = factory;
  }

  @Override
  public void populate() {
    entities.clear();
    for (T e : dbi.loadAll()) {
      if (e == null) continue;
      entities.put(e.pk, e);
    }
  }

  @Override
  public void save(T obj) {
    if (!entities.containsKey(obj.pk))
      throw new IllegalArgumentException("Tried to save entity with PK not in entities");
    if (dbi.save(obj)) {
      entities.put(obj.pk, obj);
    }
  }

  @Override
  public T get(int pk) {
    if (entities.containsKey(pk)) {
      return factory.apply(entities.get(pk));
    }
    return null;
  }

  @Override
  public T create(T obj) {
    if (obj.pk != 0) {
      throw new IllegalArgumentException("Entity to create already has a primary key");
    }
    T created = dbi.create(obj);
    if (created == null || created.pk == 0) {
      System.out.println("Error creating object");
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
      if (dbi.delete(entities.get(pk))) {
        entities.remove(pk);
        return true;
      }
    }
    return false;
  }

  @Override
  public void refreshRelations(int pk) {
    if (entities.containsKey(pk)) {
      entities.put(pk, dbi.refreshRelations(entities.get(pk)));
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
