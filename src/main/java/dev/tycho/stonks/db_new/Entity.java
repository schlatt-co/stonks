package dev.tycho.stonks.db_new;

public abstract class Entity {
  public int pk;
  public Entity(int pk) {
    this.pk = pk;
  }
}
