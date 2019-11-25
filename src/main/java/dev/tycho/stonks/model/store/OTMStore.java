package dev.tycho.stonks.model.store;

import java.util.Collection;

public interface OTMStore<P extends Entity, C extends Entity> {

  void populate();

  Collection<C> getChildren(P parent);

  P getParent(C child);

  void cacheRelation(C child);
}
