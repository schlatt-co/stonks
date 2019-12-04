package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.db_new.Entity;

import java.util.UUID;

public abstract class Account extends Entity {

  public final int companyPk;

  public final UUID uuid;

  public final String name;

  public Account(int pk, String name, UUID uuid, int companyPk) {
    super(pk);
    this.name = name;
    this.uuid = uuid;
    this.companyPk = companyPk;
  }

  public Account(Account account) {
    super(account.pk);
    this.name = account.name;
    this.uuid = account.uuid;
    this.companyPk = account.companyPk;
  }

  public abstract double getTotalBalance();

  public abstract void accept(IAccountVisitor visitor);


}
