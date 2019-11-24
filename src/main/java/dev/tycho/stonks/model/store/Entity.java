package dev.tycho.stonks.model.store;

import java.sql.*;

public abstract class Entity {
  int pk;
  public int getPk() {
    return pk;
  }

  public void setPk(int pk) {
    this.pk = pk;
  }

  public abstract void save(Connection connection);


}
