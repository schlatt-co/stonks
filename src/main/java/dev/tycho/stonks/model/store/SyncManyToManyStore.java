package dev.tycho.stonks.model.store;

public class SyncManyToManyStore<P extends Entity, C extends Entity, I extends IntermediateEntity<P, C>> extends ManyToManyStore<P, C, I> {
  public SyncManyToManyStore(Store<P> parents, Store<C> components, DatabaseInterface<I> intermediateDBI) {
    super(parents, components, new SyncStore<>(intermediateDBI));
    populate();
  }

  @Override
  public void populate() {
    for (I i : intermediates.getAll()) {
      addComponent(i);
    }
  }

  @Override
  public void addComponent(I intermediate) {
    P parent = parents.get(intermediate.getParentPk());
    C component = components.get(intermediate.getComponentPk());

    if (parent == null) throw new IllegalArgumentException("Parent object was null");
    if (component == null) throw new IllegalArgumentException("Component object was null");


    if (componentCache.containsKey(parent)) {
      //todo this assumes unique relations
      if (componentCache.get(parent).contains(component)) {
        throw new IllegalArgumentException("Tried to add a composite relation twice");
      }

      //Add our intermediate relation
      intermediates.create(intermediate);
      //And update our cache
      componentCache.get(parent).add(component);
    }
  }


}
