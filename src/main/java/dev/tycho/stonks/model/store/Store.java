package dev.tycho.stonks.model.store;

import com.google.common.collect.ImmutableList;

import java.util.function.Predicate;

public interface Store<T extends Entity> {

  T get(int pk);

  abstract void populate();

  void save(T obj);

  void create(T obj);

  ImmutableList<T> getAll();

  T getWhere(Predicate<T> p);

}
