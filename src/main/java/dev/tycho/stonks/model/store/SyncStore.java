package dev.tycho.stonks.model.store;

import java.sql.Connection;

public class SyncStore<T extends Entity> extends Store<T> {

  private Connection connection;
  public SyncStore(Connection connection) {
    this.connection = connection;
  }


  @Override
  protected void populate() {

  }

  @Override
  public T get(int pk) {
    if (entities.containsKey(pk)) return entities.get(pk);
    return null;
  }

  @Override
  public void save(T obj) {
    obj.save(connection);
  }

  @Override
  public void create(T obj) {
    if (obj.pk != 0) {
      throw new IllegalArgumentException("Entity to create already has a primary key");
    }
    obj.pk = nextPk;
    nextPk++;
    entities.put(obj.pk, obj);
    save(obj);
  }
}
