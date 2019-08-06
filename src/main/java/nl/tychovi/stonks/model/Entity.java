package nl.tychovi.stonks.model;

public abstract class Entity {
  private final int id;

  public Entity(int id) {
    this.id = id;
  }

  private boolean dirty = false;

  public void dirty() {
    dirty = true;
  }

  public void clean() {
    dirty = false;
  }

  public int id() {
    return id;
  }

}
