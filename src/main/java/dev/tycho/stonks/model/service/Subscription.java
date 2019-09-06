package dev.tycho.stonks.model.service;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "subscription")
public class Subscription {
  @DatabaseField(generatedId = true)
  private int id;

  @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, uniqueCombo = true)
  private Service service;

  @DatabaseField
  private UUID playerId;

  @DatabaseField
  private Timestamp lastPaymentDate;

  @DatabaseField
  private boolean autoPay;

  public Subscription() {
  }

  public Subscription(Player player, Service service, boolean autoPay) {
    this.playerId = player.getUniqueId();
    this.service = service;
    this.autoPay = autoPay;
    this.lastPaymentDate = new Timestamp(Calendar.getInstance().getTime().getTime());
  }

  public boolean isOverdue() {
    return (getDaysOverdue() > service.getDuration());
  }

  //Will return negative for a non-overdue date
  public long getDaysOverdue() {
    long millisDifference = Calendar.getInstance().getTimeInMillis() - lastPaymentDate.getTime();
    return (millisDifference / 86400000L);
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

  public boolean isAutoPay() {
    return autoPay;
  }



}
