package dev.tycho.stonks.model.store;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SyncOTMStore<P extends Entity, C extends  Entity> implements OTMStore<P, C> {
  protected Store<P> parents;
  protected Store<C> children;
  protected OneToMany<P, C> otm;

  protected HashMap<P, Collection<C>> relationCache;

  public SyncOTMStore(Store<P> parents, Store<C> children, OneToMany<P, C> otm) {
    relationCache = new HashMap<>();
    this.parents = parents;
    this.children = children;
    this.otm = otm;
  }

  @Override
  public void populate() {
    //Add an empty list for each parent
    for (P parent : parents.getAll()) {
      relationCache.put(parent, new ArrayList<>());
    }
    //Assign each child to the parent's list of children
    for (C child : children.getAll()) {
      cacheRelation(child);
    }
  }

  @Override
  public ImmutableCollection<C> getChildren(P parent) {
    if (relationCache.containsKey(parent)) return ImmutableList.copyOf(relationCache.get(parent));
    return null;
  }

  public P getParent(C child) {
    return parents.get(otm.getParentPk(child));
    //Todo check that we have parent and child in our stores
  }


  @Override
  public void cacheRelation(C child) {
    P parent = parents.get(otm.getChildPk(child));
    if (parent != null) {
      relationCache.get(parent).add(child);
    } else {
      //parent not found
      //assume this is ok
    }
  }
}
