package dev.tycho.stonks.model.logging;

import dev.tycho.stonks.database.Entity;

import java.sql.Timestamp;
import java.util.UUID;

public class Transaction extends Entity {
  public final int accountPk;
  public final UUID payeeUUID;
  public final String message;
  //negative amount represents money withdrawn
  public final double amount;
  public final Timestamp timestamp;


  public Transaction(int pk, int accountPk, UUID payeeUUID, String message, double amount, Timestamp timestamp) {
    super(pk);
    this.accountPk = accountPk;
    this.payeeUUID = payeeUUID;
    this.message = message;
    this.amount = amount;
    this.timestamp = timestamp;
//    this.timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
  }

  public Transaction(Transaction transaction) {
    super(transaction.pk);
    this.accountPk = transaction.accountPk;
    this.payeeUUID = transaction.payeeUUID;
    this.message = transaction.message;
    this.amount = transaction.amount;
    this.timestamp = transaction.timestamp;
  }
}
