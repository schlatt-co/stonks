package dev.tycho.stonks.model.core;

import dev.tycho.stonks.db_new.Entity;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.logging.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public abstract class Account extends Entity {

  public final String name;
  public final UUID uuid;
  public final int companyPk;
  public final Collection<Transaction> transactions;



  public Account(int pk, String name, UUID uuid, int companyPk, Collection<Transaction> transactions) {
    super(pk);
    this.name = name;
    this.uuid = uuid;
    this.companyPk = companyPk;
    this.transactions = transactions;
  }

  public Account(Account account) {
    super(account.pk);
    this.name = account.name;
    this.uuid = account.uuid;
    this.companyPk = account.companyPk;
    this.transactions = new ArrayList<>(account.transactions);
  }

  public abstract double getTotalBalance();

  public abstract void accept(IAccountVisitor visitor);


}
