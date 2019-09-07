package dev.tycho.stonks.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;

@DatabaseTable(tableName = "accountlink")
public class AccountLink {
  @DatabaseField(generatedId = true)
  private int id;

  @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
  private Company company;

  //TODO replace the double fields with a single one and a custom data persister class
  @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
  private CompanyAccount companyAccount = null;

  @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
  private HoldingsAccount holdingsAccount = null;

  public AccountLink() {

  }

  public AccountLink(Company company, Account account) {
    this.company = company;
    //Avoid reflection, determine the type of the account through a visitor
    IAccountVisitor visitor = new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        companyAccount = a;
      }

      @Override
      public void visit(HoldingsAccount a) {
        holdingsAccount = a;
      }
    };
    account.accept(visitor);
  }

  public int getId() {
    return id;
  }

  public Account getAccount() {
    return (companyAccount != null) ? companyAccount : holdingsAccount;
  }

  private AccountType getAccountType() {
    return (companyAccount != null) ? AccountType.CompanyAccount : AccountType.HoldingsAccount;
  }

  public Company getCompany() {
    return company;
  }


}
