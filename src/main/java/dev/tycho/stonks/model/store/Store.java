package dev.tycho.stonks.model.store;

import com.google.common.collect.ImmutableList;

public interface  Store<T extends Entity>  {

  abstract void populate();

  T get(int pk);

  void save(T obj);
  void create(T obj);
  ImmutableList<T> getAll();

}
