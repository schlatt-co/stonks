package dev.tycho.stonks2.model.core;

import dev.tycho.stonks2.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks2.model.service.Service;

import java.util.Collection;
import java.util.UUID;

public class CompanyAccount extends Account {
  public final double balance;

  public CompanyAccount(int pk, String name, UUID uuid, int companyPk, Collection<Service> services, double balance) {
    super(pk, name, uuid, companyPk, services);
    this.balance = balance;
  }

  public CompanyAccount(CompanyAccount companyAccount) {
    super(companyAccount);
    this.balance = companyAccount.balance;
  }

  @Override
  public double getTotalBalance() {
    return balance;
  }

  @Override
  public void accept(IAccountVisitor visitor) {
    visitor.visit(this);
  }

}
