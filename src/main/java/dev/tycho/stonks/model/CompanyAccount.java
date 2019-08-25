package dev.tycho.stonks.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;

@DatabaseTable(tableName = "companyaccount")
public class CompanyAccount extends Account {
    @DatabaseField
    private double balance;

    @Override
    public void addBalance(double amount) {
        balance += amount;
    }

    @Override
    public double getTotalBalance() {
        return balance;
    }

    public Boolean subtractBalance(double amount) {
        if(amount > balance) {
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

    public CompanyAccount(){};
    public CompanyAccount(String name) {
        super(name);
    }

}
