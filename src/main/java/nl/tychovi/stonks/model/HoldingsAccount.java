package nl.tychovi.stonks.model;

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
    public void pay(String playerUUID, double amount) {
        //Pay a fraction of the amount into each holding proportional to its share
        for (Holding h : holdings) {
            //Multiply the amount by the fractional share
            h.payIn(amount * (h.getShare() / getTotalShare()));
        }
    }

    @Override
    public boolean withdraw(String playerUUID, double amount) {
        //It is not possible to withdraw from a holdings account
        return false;
    }

    @Override
    public double getBalance() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getBalance();
        }
        return total;
    }

    public boolean AddHolding(String player, double share) {
        Holding h;
        return false;
    }
}
