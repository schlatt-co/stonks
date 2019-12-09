package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.model.core.Holding;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class HoldingDBI extends JavaSqlDBI<Holding> {


  public HoldingDBI(Connection connection) {
    super(connection);
  }

  @Override
  protected boolean createTable() {
    try {
      connection.createStatement().executeUpdate(
          "CREATE TABLE IF NOT EXISTS holding (" +
              " pk int(11) NOT NULL AUTO_INCREMENT," +
              " player_uuid varchar(36) DEFAULT NULL," +
              " balance double NOT NULL DEFAULT 0," +
              " share double NOT NULL DEFAULT 0," +
              " account_pk int(11) DEFAULT NULL," +
              " PRIMARY KEY (pk) ) "
      );
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Holding create(Holding obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "INSERT INTO holding (player_uuid, balance, share, account_pk) VALUES (?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, uuidToStr(obj.playerUUID));
      statement.setDouble(2, obj.balance);
      statement.setDouble(3, obj.share);
      statement.setInt(4, obj.accountPk);
      statement.executeUpdate();
      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        int newPk = rs.getInt(1);
        return new Holding(newPk, obj.playerUUID, obj.balance, obj.share, obj.accountPk);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Holding obj) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "DELETE FROM holding WHERE pk = ?");
      statement.setInt(1, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean save(Holding obj) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "UPDATE holding SET player_uuid = ?, balance = ?, share = ?, account_pk = ? WHERE pk = ?");
      statement.setString(1, uuidToStr(obj.playerUUID));
      statement.setDouble(2, obj.balance);
      statement.setDouble(3, obj.share);
      statement.setInt(4, obj.accountPk);
      statement.setInt(5, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Holding load(int pk) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT player_uuid, balance, share, account_pk FROM holding WHERE pk = ?");
      statement.setInt(1, pk);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        return new Holding(
            pk,
            uuidFromString(results.getString("player_uuid")),
            results.getDouble("balance"),
            results.getDouble("share"),
            results.getInt("account_pk")
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Holding refreshRelations(Holding obj) {
    //No relations
    return new Holding(obj);
  }

  @Override
  public Collection<Holding> loadAll() {
    Collection<Holding> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT pk, player_uuid, balance, share, account_pk FROM holding");

      ResultSet results = statement.executeQuery();
      while (results.next()) {
        int pk = results.getInt("pk");
        objects.add(
            new Holding(
                pk,
                uuidFromString(results.getString("player_uuid")),
                results.getDouble("balance"),
                results.getDouble("share"),
                results.getInt("account_pk")
            ));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
