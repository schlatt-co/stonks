package nl.tychovi.stonks.model;

///The company account is simply a balance that can be paid into and out of
public class CompanyAccount extends Account {


    double balance;

    public CompanyAccount(String name) {
        super(name);
    }

    @Override
    public void pay(String playerUUID, double amount) {
        //todo: create a transaction history for this
        balance += amount;
    }

    @Override
    public boolean withdraw(String playerUUID, double amount) {
        //todo: create a transaction history for this
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    @Override
    public double getBalance() {
        return balance;
    }
}
