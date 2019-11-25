package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.store.IntermediateEntity;

public class AccountLink extends IntermediateEntity<Company, Account> {



  private int companyPk;

  private int accountPk;

  private String accountType;

  public AccountLink(int companyPk, int accountPk, String accountType) {
    this.companyPk = companyPk;
    this.accountPk = accountPk;
    this.accountPk = accountPk;
  }

  public AccountLink(Company company, Account account) {
    this.companyPk = company.getPk();
    //Avoid reflection, determine the type of the account through a visitor
    IAccountVisitor visitor = new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        accountType = "CompanyAccount";
      }

      @Override
      public void visit(HoldingsAccount a) {
        accountType = "HoldingsAccount";
      }
    };
    account.accept(visitor);
    accountPk = account.getPk();
  }

  public String getAccountType() {
    return accountType;
  }

  public int getCompanyPk() {
    return companyPk;
  }

  public int getAccountPk() {
    return accountPk;
  }


  @Override
  public int getParentPk() {
    return companyPk;
  }

  @Override
  public int getComponentPk() {
    return accountPk;
  }
}
