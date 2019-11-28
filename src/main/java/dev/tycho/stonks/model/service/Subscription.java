package dev.tycho.stonks.model.service;

import dev.tycho.stonks.model.store.Entity;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

public class Subscription extends Entity {

  private int servicePk;
  private Service service;
  private UUID playerId;

  private Timestamp lastPaymentDate;


  public Subscription() {
  }

  public Subscription(Player player) {
    this.playerId = player.getUniqueId();
    this.lastPaymentDate = new Timestamp(Calendar.getInstance().getTime().getTime());
  }

  public boolean isOverdue() {
    return (getDaysOverdue() > 0);
  }

  //Will return negative for a non-overdue date
  public double getDaysOverdue() {
    long millisDifference = Calendar.getInstance().getTimeInMillis() - lastPaymentDate.getTime();
    return ((double) millisDifference / 86400000) - service.getDuration();
  }


  public void registerPaid() {
    this.lastPaymentDate = new Timestamp(Calendar.getInstance().getTime().getTime());
  }

  //Getters

  public int getServicePk() {
    return servicePk;
  }

  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public Timestamp getLastPaymentDate() {
    return lastPaymentDate;
  }


  public void setServicePk(int servicePk) {
    this.servicePk = servicePk;
  }
}
