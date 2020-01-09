package dev.tycho.stonks.database;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public abstract class DatabaseStore<T extends Entity> implements Store<T> {

  protected HashMap<Integer, T> entities = new HashMap<>();
  protected JavaSqlDBI<T> dbi;

  public void setDbi(JavaSqlDBI<T> dbi) {
    this.dbi = dbi;
  }

  protected abstract boolean db_save(T obj);

  protected abstract boolean db_delete(T obj);

  protected abstract T db_create(T obj);

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
    if (db_save(obj)) {
      entities.put(obj.pk, obj);
    }
  }

  @Override
  public T get(int pk) {
    if (entities.containsKey(pk)) {
      return entities.get(pk);
    }
    return null;
  }

  @Override
  public T create(T obj) {
    if (obj.pk != 0) {
      throw new IllegalArgumentException("Entity to create already has a primary key");
    }
    T created = db_create(obj);
    if (created == null || created.pk == 0) {
      System.out.println("Error creating object");
      return null;
    }
    if (entities.containsKey(created.pk)) {
      throw new IllegalArgumentException("Created new entity but we already have the new pk stored");
    }

    entities.put(created.pk, created);
    return created;
  }

  @Override
  public boolean delete(int pk) {
    if (entities.containsKey(pk)) {
      if (db_delete(entities.get(pk))) {
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
      if (p.test(entity)) return entity;
    }
    return null;
  }

  @Override
  public ImmutableList<T> getAllWhere(Predicate<T> p) {
    List<T> matches = new ArrayList<>();
    for (T entity : entities.values()) {
      if (p.test(entity)) {
        matches.add(entity);
      }
    }
    return ImmutableList.copyOf(matches);
  }
}
