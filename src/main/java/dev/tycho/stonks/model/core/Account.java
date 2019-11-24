package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;

import java.util.UUID;

public abstract class Account{

  private UUID uuid;

  private String name;

  public Account(String name, UUID uuid) {
    this.name = name;
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UUID getUuid() {
    return uuid;
  }

  public abstract void addBalance(double amount);

  public abstract double getTotalBalance();

  public abstract void accept(IAccountVisitor visitor);


}
