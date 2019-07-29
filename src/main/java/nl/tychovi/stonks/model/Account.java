package nl.tychovi.stonks.model;

public abstract class Account extends Entity {
    public Account(int id, String name) {
        super(id);
        this.name = name;

    }
    String name;

    public String getName() {
        return name;
    }
    public boolean setName(String name) {
        //Don't allow blank names
        if (name == null) {
            return false;
        }
        this.name = name;
        return true;
    }

    //Add money to the account
    public abstract void payIn(String playerUUID, double amount);
    //Withdraw money from the account (to pay someone / another company)
    public abstract boolean payOut(String playerUUID, double amount);
    //See the total quantity of money in the account
    public abstract double getBalance();

    public abstract void accept(IAccountVisitor visitor);

}
