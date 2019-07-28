package nl.tychovi.stonks.model;

public abstract class Account {
    public Account(String name) {
        this.name = name;
    }
    String name;

    public String getName() {
        return name;
    }
    public boolean setName(String name) {
        //Don't allow blank names
        if (name.isBlank()) {
            return false;
        }
        this.name = name;
        return true;
    }

    //Add money to the account
    public abstract void pay(String playerUUID, double amount);
    //Withdraw money from the account (to pay someone / another company)
    public abstract boolean withdraw(String playerUUID, double amount);
    //See the total quantity of money in the account
    public abstract double getBalance();


}
