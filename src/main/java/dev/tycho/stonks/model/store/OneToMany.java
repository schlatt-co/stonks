package dev.tycho.stonks.model.store;

public interface OneToMany<P extends Entity, C extends Entity> {
  int getParentPk(C child);
}
