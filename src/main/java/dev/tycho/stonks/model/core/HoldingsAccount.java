package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;

import java.util.Collection;
import java.util.UUID;

public class HoldingsAccount extends Account {

  private Collection<Holding> holdings;

  //Passing holdings this way might cause some problems, we'll see
  //Important to maintain reference equality here
  public HoldingsAccount(String name, UUID uuid, Collection<Holding> holdings) {
    super(name, uuid);
    this.holdings = holdings;
  }
  public HoldingsAccount(String name, UUID uuid) {
    super(name, uuid);
    this.holdings = holdings;
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

  public void addHolding(Holding holding) {
    this.holdings.add(holding);
  }

  public void removeHolding(Holding holding) {
    holdings.remove(holding);
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
