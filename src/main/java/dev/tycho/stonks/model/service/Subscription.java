package dev.tycho.stonks.model.service;

import dev.tycho.stonks.db_new.Entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

public class Subscription extends Entity {

  public final UUID playerId;
  public final int servicePk;
  public final Timestamp lastPaymentDate;


  public Subscription(int pk, UUID player, int servicePk, Timestamp lastPaymentDate) {
    super(pk);
    this.playerId = player;
    this.servicePk = servicePk;
    this.lastPaymentDate = lastPaymentDate;
  }

  public Subscription(Subscription subscription) {
    super(subscription.pk);
    this.playerId = subscription.playerId;
    this.servicePk = subscription.servicePk;
    this.lastPaymentDate = subscription.lastPaymentDate;
  }

  public static boolean isOverdue(Service service, Subscription subscription) {
    return (getDaysOverdue(service, subscription) > 0);
  }

  //Will return negative for a non-overdue date
  public static double getDaysOverdue(Service service, Subscription subscription) {
    long millisDifference = Calendar.getInstance().getTimeInMillis() - subscription.lastPaymentDate.getTime();
    return ((double) millisDifference / 86400000) - service.duration;
  }
}
