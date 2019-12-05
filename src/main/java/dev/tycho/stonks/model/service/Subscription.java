package dev.tycho.stonks.model.service;

import dev.tycho.stonks.db_new.Entity;

import java.sql.Date;
import java.util.Calendar;
import java.util.UUID;

public class Subscription extends Entity {

  public final UUID playerUUID;
  public final int servicePk;
  public final Date lastPaymentDate;
  public final boolean autoPay;

  public Subscription(int pk, UUID player, int servicePk, Date lastPaymentDate, boolean autoPay) {
    super(pk);
    this.playerUUID = player;
    this.servicePk = servicePk;
    this.lastPaymentDate = lastPaymentDate;
    this.autoPay = autoPay;
  }

  public Subscription(Subscription subscription) {
    super(subscription.pk);
    this.playerUUID = subscription.playerUUID;
    this.servicePk = subscription.servicePk;
    this.lastPaymentDate = subscription.lastPaymentDate;
    this.autoPay = subscription.autoPay;
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
