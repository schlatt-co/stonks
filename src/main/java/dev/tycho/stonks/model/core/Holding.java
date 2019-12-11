package dev.tycho.stonks.model.core;

import dev.tycho.stonks.database.Entity;

import java.util.UUID;

// A holding represents a share of an account held by a player
// This is done by ratio ( share1 : share2 : ...) so percentages do not need to be saved
// A player can withdraw as much money as is in the holding
public class Holding extends Entity {

  public final UUID playerUUID;
  public final int accountPk;
  public final double balance;
  public final double share;


  public Holding(int pk, UUID playerUUID, double balance, double share, int accountPk) {
    super(pk);
    if (share <= 0) {
      share = 1;
      System.out.println("Holding created with a 0 share");
    }
    this.accountPk = accountPk;
    this.playerUUID = playerUUID;
    this.share = share;
    this.balance = balance;
  }

  public Holding(Holding holding) {
    super(holding.pk);
    playerUUID = holding.playerUUID;
    balance = holding.balance;
    share = holding.share;
    accountPk = holding.accountPk;
  }
}

