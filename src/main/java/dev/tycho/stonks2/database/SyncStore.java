package dev.tycho.stonks2.database;

import java.util.function.Function;

public class SyncStore<T extends Entity> extends DatabaseStore<T> {

  public SyncStore(Function<T, T> factory) {
    super(factory);
  }

  @Override
  protected boolean db_save(T obj) {
    return dbi.save(obj);
  }

  @Override
  protected boolean db_delete(T obj) {
    return dbi.delete(obj);
  }

  @Override
  protected T db_create(T obj) {
    return dbi.create(obj);
  }
}
