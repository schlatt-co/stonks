package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.model.service.Subscription;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SubscriptionDBI extends JavaSqlDBI<Subscription> {


  public SubscriptionDBI(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected boolean createTable() {
    try {
      try (Connection conn = getConnection()) {
        conn.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS subscription (" +
                " pk int(11) NOT NULL AUTO_INCREMENT," +
                " player_uuid varchar(36) DEFAULT NULL," +
                " service_pk int(11) DEFAULT NULL," +
                " last_payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ," +
                " auto_pay bit DEFAULT NULL," +
                " PRIMARY KEY (pk) ) "
        );
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Subscription create(Subscription obj) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "INSERT INTO subscription (player_uuid, service_pk, last_payment_date, auto_pay) VALUES (?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, uuidToStr(obj.playerUUID));
        statement.setInt(2, obj.servicePk);
        statement.setTimestamp(3, obj.lastPaymentTimestamp);
        statement.setBoolean(4, obj.autoPay);
        statement.executeUpdate();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          int newPk = rs.getInt(1);
          return new Subscription(newPk, obj.playerUUID, obj.servicePk, obj.lastPaymentTimestamp, obj.autoPay);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Subscription obj) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "DELETE FROM subscription WHERE pk = ?");
        statement.setInt(1, obj.pk);
        statement.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean save(Subscription obj) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "UPDATE subscription SET player_uuid = ?, service_pk = ?, last_payment_date = ?, auto_pay = ? WHERE pk = ?");
        statement.setString(1, uuidToStr(obj.playerUUID));
        statement.setInt(2, obj.servicePk);
        statement.setTimestamp(3, obj.lastPaymentTimestamp);
        statement.setBoolean(4, obj.autoPay);
        statement.setInt(5, obj.pk);
        statement.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Subscription load(int pk) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT player_uuid, service_pk, last_payment_date, auto_pay FROM subscription WHERE pk = ?");
        statement.setInt(1, pk);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
          return new Subscription(
              pk,
              uuidFromString(results.getString("player_uuid")),
              results.getInt("service_pk"),
              results.getTimestamp("last_payment_date"),
              results.getBoolean("auto_pay")
          );
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Subscription refreshRelations(Subscription obj) {
    return new Subscription(obj);
  }

  @Override
  public Collection<Subscription> loadAll() {
    Collection<Subscription> objects = new ArrayList<>();
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT pk, player_uuid, service_pk, last_payment_date, auto_pay FROM subscription");

        ResultSet results = statement.executeQuery();
        while (results.next()) {
          int pk = results.getInt("pk");
          objects.add(
              new Subscription(
                  pk,
                  uuidFromString(results.getString("player_uuid")),
                  results.getInt("service_pk"),
                  results.getTimestamp("last_payment_date"),
                  results.getBoolean("auto_pay")
              ));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
