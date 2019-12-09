package dev.tycho.stonks.database;

import com.google.common.collect.ImmutableList;

import java.util.function.Predicate;

public interface Store<T extends Entity> {


  /**
   * Gets an entity by its primary key
   *
   * @param pk The primary key of the entity to retrieve
   * @return The entity with pk
   */
  T get(int pk);

  /**
   * Get an entity that matches a predicate
   * Note: the entity retrieved is not guaranteed to have the lowest PK of any matched entities.
   *
   * @param p Predicate to test each entity against
   * @return An object that matches this predicate
   */
  T getWhere(Predicate<T> p);

  /**
   * Loads all entities into the store from whatever concrete source exists
   */
  void populate();

  /**
   * Update an entity's fields in the store
   *
   * @param obj The entity to update
   */
  void save(T obj);

  /**
   * Adds a new entity to the store.
   *
   * @param obj The entity to add to the store
   * @return A copy of the same entity with a generated primary key (pk)
   */
  T create(T obj);

  /**
   * Delete an entity from the store
   * @param pk The primary key of the entity to delete
   * @return True if the operation was successful, false if the delete operation failed (e.g deleting not supported)
   */
  boolean delete(int pk);


  /**
   * Updates any child collections the entity has
   * @param pk The primary key of the entity to refresh
   */
  void refreshRelations(int pk);


  /**
   * @return All entities in the store
   */
  ImmutableList<T> getAll();


  /**
   * @param p Predicate to test entities against
   * @return All entities in the store that match the predicate
   */
  ImmutableList<T> getAllWhere(Predicate<T> p);

}
