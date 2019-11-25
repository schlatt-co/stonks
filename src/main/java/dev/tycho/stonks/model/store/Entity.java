package dev.tycho.stonks.model.store;

public abstract class Entity {
  private int pk;
  public int getPk() {
    return pk;
  }

  public void setPk(int pk) {
    this.pk = pk;
  }
}
