package dev.tycho.stonks.model.store;

import java.util.Collection;

public interface ForeignKey<P extends Entity, C extends Entity> {
  int getParentPk(C child);
  default void onCreate(P parent, Collection<C> children){};
}
