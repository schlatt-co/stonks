package dev.tycho.stonks.model.core;

import dev.tycho.stonks.db_new.Entity;

import java.util.UUID;

// A holding represents a share of an account held by a player
// This is done by ratio ( share1 : share2 : ...) so percentages do not need to be saved
// A player can withdraw as much money as is in the holding
public class Holding extends Entity {

  public final UUID player;

  public final double balance;

  public final double share;

  public final int accountPk;

  public Holding(int pk, UUID player, int accountPk, double share, double balance) {
    super(pk);
    if (share <= 0) {
      share = 1;
      System.out.println("Holding created with a 0 share");
    }
    this.accountPk = accountPk;
    this.player = player;
    this.share = share;
    this.balance = balance;
  }

  public Holding(Holding holding) {
    super(holding.pk);
    player = holding.player;
    balance = holding.balance;
    share = holding.share;
    accountPk = holding.accountPk;
  }
}

