package dev.tycho.stonks.database;

import com.google.common.collect.ImmutableList;

import java.util.function.Predicate;

public interface Store<T extends Entity> {

  T get(int pk);

  void populate();

  void save(T obj);

  T create(T obj);

  boolean delete(int pk);

  void refresh(int pk);

  ImmutableList<T> getAll();


  T getWhere(Predicate<T> p);

  ImmutableList<T> getAllWhere(Predicate<T> p);

}
