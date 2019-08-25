package dev.tycho.stonks.Database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DatabaseTable(tableName = "holdingsaccount")
public class HoldingsAccount extends Account {

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Holding> holdings;

    public HoldingsAccount() {
    }

    public HoldingsAccount(String name) {
        super(name);
    }

    public double getTotalShare() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getShare();
        }
        return total;
    }

    public ForeignCollection<Holding> getHoldings() {
        return holdings;
    }

    //Returns the holding for the player entered, if none is found then return nothing
    public Holding getPlayerHolding(UUID player) {
        for (Holding h : holdings) {
            if (h.getPlayer().equals(player)) {
                return h;
            }
        }
        return null;
    }

    public void addHolding(Holding holding) {
        this.holdings.add(holding);
    }

    public boolean removeHolding(Holding holding) {
        if (holdings.contains(holding)) {
            holdings.remove(holding);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void addBalance(double amount) {
        //Pay a fraction of the amount into each holding proportional to its share
        for (Holding h : holdings) {
            //Multiply the amount by the fractional share
            h.payIn(amount * (h.getShare() / getTotalShare()));
        }
    }

    @Override
    public double getTotalBalance() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getBalance();
        }
        return total;
    }

    @Override
    public void accept(IAccountVisitor visitor) {
        visitor.visit(this);
    }
}
