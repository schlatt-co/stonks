package dev.tycho.stonks.model.store;

public abstract class IntermediateEntity<P, C> extends Entity {
  public abstract int getParentPk();
  public abstract int getComponentPk();
}
