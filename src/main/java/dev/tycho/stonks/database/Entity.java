package dev.tycho.stonks.database;

public abstract class Entity {
  public int pk;
  public Entity(int pk) {
    this.pk = pk;
  }
}
