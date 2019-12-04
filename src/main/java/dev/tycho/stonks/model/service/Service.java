package dev.tycho.stonks.model.service;


import dev.tycho.stonks.db_new.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Service extends Entity {

  public final String name;

  //days
  public final double duration;

  public final double cost;

  //0 = no limit
  public final int maxSubscribers;

  public final int accountPk;
  public final Collection<Subscription> subscriptions;

  public Service(int pk, String name, double duration, double cost, int maxSubscribers, int accountPk, Collection<Subscription> subscriptions) {
    super(pk);
    this.name = name;
    this.duration = duration;
    this.cost = cost;
    this.maxSubscribers = maxSubscribers;
    this.accountPk = accountPk;
    this.subscriptions = subscriptions;
  }

  public Service(Service service) {
    super(service.pk);
    this.name = service.name;
    this.duration = service.duration;
    this.cost = service.cost;
    this.maxSubscribers = service.maxSubscribers;
    this.accountPk = service.accountPk;
    this.subscriptions = service.subscriptions;
  }


  //Returns the subscription for a player.
  //If none is found then return null
  public Subscription getSubscription(Player player) {
    for (Subscription s : subscriptions) {
      if (s.playerId.equals(player.getUniqueId())) return s;
    }
    return null;
  }
}
