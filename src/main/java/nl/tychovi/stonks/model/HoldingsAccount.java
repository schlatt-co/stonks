package nl.tychovi.stonks.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HoldingsAccount extends Account {

    private List<Holding> holdings = new ArrayList<>();

    public HoldingsAccount(int id, String name, Holding firstHolding) {
        super(id, name);
        holdings.add(firstHolding);
    }
    public HoldingsAccount(int id, String name) {
        super(id, name);
    }

    public double getTotalShare() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getShare();
        }
        return total;
    }

    //Returns the holding for the player entered, if none is found then return nothing
    private Holding getPlayerHolding(UUID player) {
        for (Holding h: holdings){
            if (h.player_uuid.equals(player)) {
                return h;
            }
        }
        return null;
    }

    @Override
    public void payIn(UUID playerUUID, double amount) {
        //Pay a fraction of the amount into each holding proportional to its share
        for (Holding h : holdings) {
            //Multiply the amount by the fractional share
            h.payIn(amount * (h.getShare() / getTotalShare()));
        }
    }

    @Override
    public boolean payOut(UUID playerUUID, double amount) {
        //It is not possible to withdraw from a holdings account
        return false;
    }

    //Withdraws an amount from a player's holding
    public boolean withdraw(UUID player, double amount) {
        Holding h = getPlayerHolding(player);
        if (h != null) {
            //Try and withdraw that amount
            return h.withdraw(amount);
        } else {
            //There is no holding for this player
            return false;
        }
    }

    @Override
    public double getBalance() {
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

    //Returns true if holding added
    // Else returns false
    public boolean addHolding(Holding h) {
        if (holdings.contains(h)) {
            return false;
        } else {
            holdings.add(h);
            return true;
        }

    }
    public boolean removeHolding(UUID player) {
        Holding h = getPlayerHolding(player);
        if (h != null) {
            holdings.remove(h);
            //todo: pay the player the money in the holding
            //todo: handle holding deleted in database
            h.withdraw(h.getBalance());
            return true;
        } else {
            return false;
        }
    }
    public List<Holding> getHoldings() {
        return holdings;
    }
}
