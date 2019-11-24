package dev.tycho.stonks.model.store;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;

public abstract class Store<T extends Entity>  {
  protected HashMap<Integer, T> entities = new HashMap<>();
  protected abstract void populate();

  public abstract T get(int pk);
  public abstract void save(T obj);
  public abstract void create(T obj);
  public ImmutableList<T> getAll() {
    return ImmutableList.copyOf(entities.values());
  }

}
