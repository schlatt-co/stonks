package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;

import java.util.Collection;
import java.util.UUID;

public class HoldingsAccount extends Account {

  private Collection<Holding> holdings;

  public HoldingsAccount(String name, UUID uuid) {
    super(name, uuid);
  }
  public void setHoldings(Collection<Holding> holdings) {
    this.holdings = holdings;
  }

  private double getTotalShare() {
    double total = 0;
    for (Holding h : holdings) {
      total += h.getShare();
    }
    return total;
  }

  public Collection<Holding> getHoldings() {
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
