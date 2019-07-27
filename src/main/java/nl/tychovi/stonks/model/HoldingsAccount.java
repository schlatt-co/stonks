package nl.tychovi.stonks.model;

import java.lang.management.PlatformLoggingMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HoldingsAccount extends Account {

    private List<Holding> holdings = new ArrayList<Holding>();

    public HoldingsAccount(String name, Holding firstHolding) {
        super(name);
        holdings.add(firstHolding);
    }

    private double getTotalShare() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getShare();
        }
        return total;
    }

    //Returns the holding for the player entered, if none is found then return nothing
    private Optional<Holding> getPlayerHolding(String player) {
        for (Holding h: holdings){
            if (h.player.equals(player)) {
                return Optional.of(h);
            }
        }
        return Optional.empty();
    }

    @Override
    public void payIn(String playerUUID, double amount) {
        //Pay a fraction of the amount into each holding proportional to its share
        for (Holding h : holdings) {
            //Multiply the amount by the fractional share
            h.payIn(amount * (h.getShare() / getTotalShare()));
        }
    }

    @Override
    public boolean payOut(String playerUUID, double amount) {
        //It is not possible to withdraw from a holdings account
        return false;
    }

    //Withdraws an amount from a player's holding
    public boolean withdraw(String player, double amount) {
        var h = getPlayerHolding(player);
        if (h.isPresent()) {
            //Try and withdraw that amount
            return h.get().withdraw(amount);
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

    //Returns true if holding added
    // Else returns false
    public boolean addHolding(String player, double share) {
        //Don't allow duplicate holdings
        if (getPlayerHolding(player).isPresent()) return false;
        if (share > 0) {
            holdings.add(new Holding(player, share));
            return true;
        } else {
            return false;
        }

    }
    public boolean removeHolding(String player) {
        var h = getPlayerHolding(player);
        if (h.isPresent()) {
            holdings.remove(h.get());
            //todo: pay the player the money in the holding
            h.get().withdraw(h.get().getBalance());
            return true;
        } else {
            return false;
        }
    }
}
