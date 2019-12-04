package dev.tycho.stonks.model.store_old;

import dev.tycho.stonks.db_new.Entity;

import java.util.Collection;

public interface ForeignKey<P extends Entity, C extends Entity> {
  int getParentPk(C child);
  default void createParentReference(P parent, Collection<C> children){};
  default void createChildReference(C child, P parent){};
}
