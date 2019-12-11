package dev.tycho.stonks.model.trading;

import dev.tycho.stonks.database.Entity;

import java.sql.Timestamp;
import java.util.UUID;

public class Shareholder extends Entity {
  public final UUID playerUUID;
  public final int stockPk;
  public final double numShares;
  public final Timestamp lastPurchase;
  public final Timestamp lastSale;

  public Shareholder(int pk, UUID playerUUID, int stockPk, double numShares, Timestamp lastPurchase, Timestamp lastSale) {
    super(pk);
    this.playerUUID = playerUUID;
    this.stockPk = stockPk;
    this.numShares = numShares;
    this.lastPurchase = lastPurchase;
    this.lastSale = lastSale;
  }

  public Shareholder(Shareholder shareholder) {
    super(shareholder.pk);
    this.playerUUID = shareholder.playerUUID;
    this.stockPk = shareholder.stockPk;
    this.numShares = shareholder.numShares;
    this.lastPurchase = shareholder.lastPurchase;
    this.lastSale = shareholder.lastSale;
  }

}
