package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.store.Entity;

import java.util.UUID;

// A holding represents a share of an account held by a player
// This is done by ratio ( share1 : share2 : ...) so percentages do not need to be saved
// A player can withdraw as much money as is in the holding
public class Holding extends Entity {

  private UUID player;

  private double balance;

  private double share;

  private int accountPk;

  private HoldingsAccount account;



  public Holding() {
  }

  public Holding(UUID player, HoldingsAccount account, double share) {
    if (share <= 0) {
      share = 1;
      System.out.println("Holding created with a 0 share");
    }
    this.accountPk = account.getPk();
    this.player = player;
    this.share = share;
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

  public void setBalance(double amount) {
    this.balance = amount;
  }


  public boolean subtractBalance(double amount) {
    if (balance < amount) return false;
    balance -= amount;
    return true;
  }

  public double getBalance() {
    return balance;
  }

  public UUID getPlayer() {
    return player;
  }

  public int getAccountPk() {
    return accountPk;
  }


  public HoldingsAccount getAccount() {
    return account;
  }

  public void setAccount(HoldingsAccount account) {
    this.account = account;
  }
}

