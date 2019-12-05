package dev.tycho.stonks.model.core;

import dev.tycho.stonks.db_new.Entity;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public abstract class Account extends Entity {

  public final String name;
  public final UUID uuid;
  public final int companyPk;
  public final Collection<Transaction> transactions;
  public final Collection<Service> services;


  public Account(int pk, String name, UUID uuid, int companyPk, Collection<Transaction> transactions, Collection<Service> services) {
    super(pk);
    this.name = name;
    this.uuid = uuid;
    this.companyPk = companyPk;
    this.transactions = transactions;
    this.services = services;
  }

  public Account(Account account) {
    super(account.pk);
    this.name = account.name;
    this.uuid = account.uuid;
    this.companyPk = account.companyPk;
    this.transactions = new ArrayList<>(account.transactions);
    this.services = new ArrayList<>(account.services);
  }

  public abstract double getTotalBalance();

  public abstract void accept(IAccountVisitor visitor);


}
