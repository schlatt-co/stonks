package dev.tycho.stonks.model.service;

import dev.tycho.stonks.model.store.Entity;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

public class Subscription extends Entity {

  private int servicePk;

  private UUID playerId;

  private Timestamp lastPaymentDate;


  public Subscription() {
  }

  public Subscription(Player player, Service service, Timestamp lastPaymentDate) {
    this.playerId = player.getUniqueId();
    this.servicePk = service.getPk();
    this.lastPaymentDate = lastPaymentDate; //= new Timestamp(Calendar.getInstance().getTime().getTime());
  }

  public static boolean isOverdue(Service service, Subscription subscription) {
    return (getDaysOverdue(service, subscription) > 0);
  }

  //Will return negative for a non-overdue date
  public static double getDaysOverdue(Service service, Subscription subscription) {
    long millisDifference = Calendar.getInstance().getTimeInMillis() - subscription.lastPaymentDate.getTime();
    return ((double) millisDifference / 86400000) - service.getDuration();
  }


  public void registerPaid() {
    this.lastPaymentDate = new Timestamp(Calendar.getInstance().getTime().getTime());
  }

  //Getters

  public int getService() {
    return servicePk;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public Timestamp getLastPaymentDate() {
    return lastPaymentDate;
  }



}
