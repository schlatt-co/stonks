package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;

import java.util.UUID;

public class CompanyAccount extends Account {
  private double balance;

  public CompanyAccount(String name, UUID uuid) {
    super(name, uuid);
  }

  @Override
  public void addBalance(double amount) {
    balance += amount;
  }

  @Override
  public double getTotalBalance() {
    return balance;
  }

  public Boolean subtractBalance(double amount) {
    if (amount > balance) {
      return false;
    } else {
      balance -= amount;
      return true;
    }
  }

  @Override
  public void accept(IAccountVisitor visitor) {
    visitor.visit(this);
  }

}
