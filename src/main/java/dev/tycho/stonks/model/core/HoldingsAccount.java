package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class HoldingsAccount extends Account {

  public final Collection<Holding> holdings;

  public HoldingsAccount(int pk, String name, UUID uuid, int companyPk, Collection<Holding>holdings) {
    super(pk, name, uuid, companyPk);
    this.holdings = holdings;
  }

  public HoldingsAccount(HoldingsAccount holdingsAccount) {
    super(holdingsAccount);
    this.holdings = new ArrayList<>(holdingsAccount.holdings);
  }


  public double getTotalShare() {
    double total = 0;
    for (Holding h : holdings) {
      total += h.share;
    }
    return total;
  }

  //Returns the holding for the player entered, if none is found then return nothing
  public Holding getPlayerHolding(UUID player) {
    for (Holding h : holdings) {
      if (h.player.equals(player)) {
        return h;
      }
    }
    return null;
  }
  @Override
  public double getTotalBalance() {
    double total = 0;

    for (Holding h : holdings) {
      total += h.balance;
    }
    return total;
  }

  @Override
  public void accept(IAccountVisitor visitor) {
    visitor.visit(this);
  }
}
