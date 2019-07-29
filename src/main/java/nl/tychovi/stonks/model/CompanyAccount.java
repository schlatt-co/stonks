package nl.tychovi.stonks.model;

///The company account is simply a balance that can be paid into and out of
public class CompanyAccount extends Account {


    double balance;

    public CompanyAccount(int id, String name, double balance) {
        super(id, name);
        this.balance = balance;
    }

    @Override
    public void payIn(String playerUUID, double amount) {
        //todo: create a transaction history for this
        balance += amount;
    }

    @Override
    public boolean payOut(String playerUUID, double amount) {
        //todo: create a transaction history for this
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void accept(IAccountVisitor visitor) {
        visitor.visit(this);
    }
}
