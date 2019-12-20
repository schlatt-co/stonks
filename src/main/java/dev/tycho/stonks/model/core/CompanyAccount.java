package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.service.Service;

import java.util.Collection;
import java.util.UUID;

public class CompanyAccount extends Account {
  public final double balance;

  public CompanyAccount(int pk, String name, UUID uuid, int companyPk, boolean profitAccount, Collection<Service> services, double balance) {
    super(pk, name, uuid, companyPk, profitAccount, services);
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
