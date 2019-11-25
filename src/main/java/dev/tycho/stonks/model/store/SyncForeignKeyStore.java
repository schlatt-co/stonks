package dev.tycho.stonks.model.store;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SyncForeignKeyStore<P extends Entity, C extends Entity> implements ForeignKeyStore<P, C> {
  private HashMap<P, Collection<C>> relationCache;
  private Store<P> parents;
  private Store<C> children;
  private ForeignKey<P, C> key;

  public SyncForeignKeyStore(Store<P> parents, Store<C> children, ForeignKey<P, C> key) {
    relationCache = new HashMap<>();
    this.parents = parents;
    this.children = children;
    this.key = key;
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
    return parents.get(key.getParentPk(child));
    //Todo check that we have parent and child in our stores
  }


  @Override
  public void cacheRelation(C child) {
    P parent = parents.get(child.getPk());
    if (parent != null) {
      relationCache.get(parent).add(child);
    } else {
      //parent not found
      //assume this is ok
    }
  }
}
