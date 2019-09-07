package dev.tycho.stonks.model.service;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import dev.tycho.stonks.model.core.AccountLink;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

@DatabaseTable(tableName = "service")
public class Service {
  @DatabaseField(generatedId = true)
  private int id;

  @DatabaseField
  private String name;

  //days
  @DatabaseField
  private double duration;

  @DatabaseField
  private double cost;

  //0 = no limit
  @DatabaseField
  private int maxSubscribers;

  @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
  private Company company;

  @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
  private AccountLink account;

  @ForeignCollectionField(eager = true)
  private ForeignCollection<Subscription> subscriptions;

  public Service() {
  }

  public Service(String name, Company company, AccountLink account, double duration, double cost, int maxSubscribers) {
    this.name = name;
    this.company = company;
    this.account = account;
    this.duration = duration;
    this.cost = cost;
    this.maxSubscribers = maxSubscribers;
  }

  //Returns the subscription for a player.
  //If none is found then return null
  public Subscription getSubscription(Player player) {
    for (Subscription s : subscriptions) {
      if (s.getPlayerId().equals(player.getUniqueId())) return s;
    }
    return null;
  }

  public Subscription cancelSubscription(Player player) {
    Subscription s = getSubscription(player);
    if (s == null) {
      return null;
    } else {
      subscriptions.remove(s);
      return s;
    }
  }

  public Collection<Subscription> getOverdueSubscriptions() {
    Collection<Subscription> overdue = new ArrayList<>();
    for (Subscription s : subscriptions) {
      if (s.isOverdue()) overdue.add(s);
    }
    return overdue;
  }

  public void setMaxSubscribers(int maxSubscribers) {
    this.maxSubscribers = maxSubscribers;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getDuration() {
    return duration;
  }

  public double getCost() {
    return cost;
  }

  public int getMaxSubscriptions() {
    return maxSubscribers;
  }

  public Collection<Subscription> getSubscriptions() {
    return subscriptions;
  }
  public int getNumSubscriptions() {
    return subscriptions.size();
  }


  public Company getCompany() {
    return company;
  }

  public AccountLink getAccount() {
    return account;
  }
}
