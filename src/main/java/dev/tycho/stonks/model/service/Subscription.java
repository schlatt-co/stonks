package dev.tycho.stonks.model.service;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

@DatabaseTable(tableName = "subscription")
public class Subscription {
  @DatabaseField(generatedId = true)
  private int id;

  @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
  private Service service;

  @DatabaseField
  private UUID playerId;

  @DatabaseField
  private Timestamp lastPaymentDate;


  public Subscription() {
  }

  public Subscription(Player player, Service service) {
    this.playerId = player.getUniqueId();
    this.service = service;
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

  public int getId() {
    return id;
  }

  public Service getService() {
    return service;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public Timestamp getLastPaymentDate() {
    return lastPaymentDate;
  }



}
