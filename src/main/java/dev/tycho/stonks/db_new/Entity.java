package dev.tycho.stonks.db_new;

public abstract class Entity {
  public final int pk;
  public Entity(int pk) {
    this.pk = pk;
  }
}
