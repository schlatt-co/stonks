package dev.tycho.stonks.model.store_old;

import dev.tycho.stonks.db_new.Entity;

public abstract class IntermediateEntity<P, C> extends Entity {
  public abstract int getParentPk();
  public abstract int getComponentPk();
}
