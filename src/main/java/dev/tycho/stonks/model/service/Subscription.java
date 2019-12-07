package dev.tycho.stonks.model.service;

import dev.tycho.stonks.database.Entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

public class Subscription extends Entity {

  public final UUID playerUUID;
  public final int servicePk;
  public final Timestamp lastPaymentTimestamp;
  public final boolean autoPay;

  public Subscription(int pk, UUID player, int servicePk, Timestamp lastPaymentTimestamp, boolean autoPay) {
    super(pk);
    this.playerUUID = player;
    this.servicePk = servicePk;
    this.lastPaymentTimestamp = lastPaymentTimestamp;
    this.autoPay = autoPay;
  }

  public Subscription(Subscription subscription) {
    super(subscription.pk);
    this.playerUUID = subscription.playerUUID;
    this.servicePk = subscription.servicePk;
    this.lastPaymentTimestamp = subscription.lastPaymentTimestamp;
    this.autoPay = subscription.autoPay;
  }

  public static boolean isOverdue(Service service, Subscription subscription) {
    return (getDaysOverdue(service, subscription) > 0);
  }

  //Will return negative for a non-overdue date
  public static double getDaysOverdue(Service service, Subscription subscription) {
    long millisDifference = Calendar.getInstance().getTimeInMillis() - subscription.lastPaymentTimestamp.getTime();
    return ((double) millisDifference / 86400000) - service.duration;
  }
}
