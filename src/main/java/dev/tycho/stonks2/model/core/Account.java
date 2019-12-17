package dev.tycho.stonks2.model.core;

import dev.tycho.stonks2.database.Entity;
import dev.tycho.stonks2.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks2.model.service.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public abstract class Account extends Entity {

  public final String name;
  public final UUID uuid;
  public final int companyPk;
  public final Collection<Service> services;


  public Account(int pk, String name, UUID uuid, int companyPk, Collection<Service> services) {
    super(pk);
    this.name = name;
    this.uuid = uuid;
    this.companyPk = companyPk;
    this.services = services;
  }

  public Account(Account account) {
    super(account.pk);
    this.name = account.name;
    this.uuid = account.uuid;
    this.companyPk = account.companyPk;
    this.services = new ArrayList<>(account.services);
  }

  public abstract double getTotalBalance();

  public abstract void accept(IAccountVisitor visitor);


}
