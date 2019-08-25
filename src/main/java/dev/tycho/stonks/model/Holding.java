package dev.tycho.stonks.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import dev.tycho.stonks.database.HoldingDaoImpl;

import java.util.UUID;

// A holding represents a share of an account held by a player
// This is done by ratio ( share1 : share2 : ...) so percentages do not need to be saved
// A player can withdraw as much money as is in the holding
@DatabaseTable(tableName = "holding", daoClass = HoldingDaoImpl.class)
public class Holding {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField()
    private UUID player;

    @DatabaseField()
    private double balance;

    @DatabaseField()
    private double share;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private HoldingsAccount holdingsAccount = null;

    public Holding() {
    }

    public Holding(UUID player, double share, HoldingsAccount holdingsAccount) {
        if (share <= 0) {
            share = 1;
            System.out.println("Holding created with a 0 share");
        }
        this.player = player;
        this.share = share;
        this.holdingsAccount = holdingsAccount;
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
        this.balance += amount;
    }

    public boolean subtractBalance(double amount) {
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    public double getBalance() {
        return balance;
    }

    public int getId() {
        return id;
    }

    public UUID getPlayer() {
        return player;
    }
}

