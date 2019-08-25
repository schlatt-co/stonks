package dev.tycho.stonks.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
        final AccountType[] type = new AccountType[1];
        IAccountVisitor visitor = new IAccountVisitor() {
            @Override
            public void visit(CompanyAccount a) {
                type[0] = AccountType.CompanyAccount;
            }

            @Override
            public void visit(HoldingsAccount a) {
                type[0] = AccountType.HoldingsAccount;
            }
        };
        account.accept(visitor);

        //Only set one of the two account types to a reference
        //The other will stay null
        switch (type[0]) {
            case CompanyAccount:
                this.companyAccount = (CompanyAccount)account;
                break;
            case HoldingsAccount:
                this.holdingsAccount = (HoldingsAccount)account;
                break;
        }
    }

    public int getId() {
        return id;
    }

    public Account getAccount() {
        return (companyAccount != null) ? companyAccount : holdingsAccount;
    }

    public AccountType getAccountType() {
        return (companyAccount != null) ? AccountType.CompanyAccount : AccountType.HoldingsAccount;
    }

    public Company getCompany() {
        return company;
    }


}
