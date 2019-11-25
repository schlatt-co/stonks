package dev.tycho.stonks.model.store;

public interface ForeignKey<P extends Entity, C extends Entity> {
  int getParentPk(C child);
}
