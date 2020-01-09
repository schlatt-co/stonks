package dev.tycho.stonks.model.core;

import dev.tycho.stonks.database.Entity;

public class Perk extends Entity {
  public final int companyPk;
  public final String namespace;

  public Perk(int pk, int companyPk, String namespace) {
    super(pk);
    this.companyPk = companyPk;
    this.namespace = namespace;
  }
}
