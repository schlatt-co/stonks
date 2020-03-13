package dev.tycho.stonks.model.core;


import uk.tsarcasm.tsorm.Entity;

public class Perk extends Entity {
  public final int companyPk;
  public final String namespace;

  public Perk(int pk, int companyPk, String namespace) {
    super(pk);
    this.companyPk = companyPk;
    this.namespace = namespace;
  }

  public Perk(Perk other) {
    super(other.pk);
    this.companyPk = other.companyPk;
    this.namespace = other.namespace;
  }
}
