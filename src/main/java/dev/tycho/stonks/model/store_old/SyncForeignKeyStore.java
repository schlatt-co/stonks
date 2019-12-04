package dev.tycho.stonks.model.store_old;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.tycho.stonks.db_new.Entity;
import dev.tycho.stonks.db_new.Store;

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
      putParent(parent);
    }
    //Assign each child to the parent's list of children
    for (C child : children.getAll()) {
      putChild(child);
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
  public Collection<C> putParent(P parent) {
    if (parents.get(parent.getPk()) == null) {
      throw new IllegalArgumentException("Couldn't find parent PK");
    }
    if (!relationCache.containsKey(parent)) {
      relationCache.put(parent, new ArrayList<>());
      key.createParentReference(parent, relationCache.get(parent));
    }
    return relationCache.get(parent);
  }


  @Override
  public void putChild(C child) {
    P parent = parents.get(child.getPk());
    if (parent != null) {
      relationCache.get(parent).add(child);
      key.createChildReference(child, parent);
    } else {
      //parent not found
      //assume this is ok
    }
  }
}
