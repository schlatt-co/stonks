package dev.tycho.stonks.model.service;


import dev.tycho.stonks.model.core.AccountLink;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.store.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Service extends Entity {

  private String name;

  //days
  private double duration;

  private double cost;

  //0 = no limit
  private int maxSubscribers;
  private int companyPk;
  private Company company;

  private int accountPk;
  private Collection<Subscription> subscriptions;
  public Service() {
  }

  public Service(String name, Company company, AccountLink account, Collection<Subscription> subscriptions, double duration, double cost, int maxSubscribers) {
    this.name = name;
    this.companyPk = company.getPk();
    this.accountPk = account.getPk();
    this.duration = duration;
    this.cost = cost;
    this.maxSubscribers = maxSubscribers;
    this.subscriptions = subscriptions;
  }

  //Returns the subscription for a player.
  //If none is found then return null
  public Subscription getSubscription(Player player) {
    for (Subscription s : subscriptions) {
      if (s.getPlayerId().equals(player.getUniqueId())) return s;
    }
    return null;
  }


  public void setMaxSubscribers(int maxSubscribers) {
    this.maxSubscribers = maxSubscribers;
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

  public void setSubscriptions(Collection<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  public int getNumSubscriptions() {
    return subscriptions.size();
  }


  public int getCompanyPk() {
    return companyPk;
  }

  public int getAccountPk() {
    return accountPk;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }
}
