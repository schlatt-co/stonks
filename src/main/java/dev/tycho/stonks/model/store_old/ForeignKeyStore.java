package dev.tycho.stonks.model.store_old;

import dev.tycho.stonks.db_new.Entity;

import java.util.Collection;

public interface ForeignKeyStore<P extends Entity, C extends Entity> {

  void populate();

  Collection<C> getChildren(P parent);

  P getParent(C child);
  Collection<C> putParent(P parent);
  void putChild(C child);
}
