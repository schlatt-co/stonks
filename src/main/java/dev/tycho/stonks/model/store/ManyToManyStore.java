package dev.tycho.stonks.model.store;

import java.util.Collection;
import java.util.HashMap;

public abstract class ManyToManyStore<P extends Entity, C extends Entity, I extends IntermediateEntity<P, C>> {
  protected HashMap<P, Collection<C>> componentCache;
  protected Store<P> parents;
  protected Store<C> components;
  protected Store<I> intermediates;

  public ManyToManyStore(Store<P> parents, Store<C> components, Store<I> intermediates) {
    this.parents = parents;
    this.components = components;
    this.intermediates = intermediates;
  }

  public abstract void populate();

  public Collection<C> getComponents(P parent) {
    if (componentCache.containsKey(parent)) return componentCache.get(parent);
    return null;
  }

  public abstract void addComponent(I intermediate);


}
