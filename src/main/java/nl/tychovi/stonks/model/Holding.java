package nl.tychovi.stonks.model;

// A holding represents a share of an account held by a player
// This is done by ratio ( share1 : share2 : ...) so percentages do not need to be saved
// A player can withdraw as much money as is in the holding
public class Holding extends Entity {
    String player;
    double balance;
    double share;

    public Holding(int id, String player, double share, double balance) {
        super(id);
        if (player == null) throw new IllegalArgumentException("Player string was blank");
        if (share <= 0) throw new IllegalArgumentException("The holding share cannot be <= 0");
        this.player = player;
        this.share = share;
        this.balance = balance;
    }

    public double getShare() {
        return share;
    }
    public boolean setShare(double share) {
        if (share > 0) {
            this.share = share;
            return true;
        } else {
            //Share must be positive and not 0
            return false;
        }
    }

    public void payIn(double amount) {
        this.balance += balance;
    }
    public boolean withdraw(double amount) {
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }
    public double getBalance() {
        return balance;
    }


}
