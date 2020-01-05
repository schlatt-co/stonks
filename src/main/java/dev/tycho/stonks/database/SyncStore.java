package dev.tycho.stonks.database;

public class SyncStore<T extends Entity> extends DatabaseStore<T> {
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
