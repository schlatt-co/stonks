package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.database.Store;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class ServiceDBI extends JavaSqlDBI<Service> {

  private final Store<Subscription> subscriptionStore;


  public ServiceDBI(Connection connection, Store<Subscription> subscriptionStore) {
    super(connection);
    this.subscriptionStore = subscriptionStore;
  }

  @Override
  protected boolean createTable() {
    try {
      connection.createStatement().executeUpdate(
          "CREATE TABLE IF NOT EXISTS service (" +
              " pk int(11) NOT NULL AUTO_INCREMENT," +
              " name varchar(255) DEFAULT NULL," +
              " duration double DEFAULT 0," +
              " cost double DEFAULT 0," +
              " max_subscribers int(11) DEFAULT 0," +
              " account_pk int(11) DEFAULT NULL," +
              " PRIMARY KEY (pk)) "
      );
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Service create(Service obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "INSERT INTO service (name, duration, cost, max_subscribers, account_pk) VALUES (?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, obj.name);
      statement.setDouble(2, obj.duration);
      statement.setDouble(3, obj.cost);
      statement.setInt(4, obj.maxSubscribers);
      statement.setInt(5, obj.accountPk);
      statement.executeUpdate();
      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        int newPk = rs.getInt(1);
        return new Service(newPk, obj.name, obj.duration, obj.cost, obj.maxSubscribers, obj.accountPk, new ArrayList<>());
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Service obj) {
    //We dont support deleting services at this time
    return false;
  }

  @Override
  public boolean save(Service obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "UPDATE service SET name = ?, duration = ?, cost = ?, max_subscribers = ?, account_pk = ? WHERE pk = ?");
      statement.setString(1, obj.name);
      statement.setDouble(2, obj.duration);
      statement.setDouble(3, obj.cost);
      statement.setInt(4, obj.maxSubscribers);
      statement.setInt(5, obj.accountPk);
      statement.setInt(6, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Service load(int pk) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT name, duration, cost, max_subscribers, account_pk FROM service WHERE pk = ?");
      statement.setInt(1, pk);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        return new Service(
            pk,
            results.getString("name"),
            results.getDouble("duration"),
            results.getDouble("cost"),
            results.getInt("max_subscribers"),
            results.getInt("account_pk"),
            new ArrayList<>(subscriptionStore.getAllWhere(s -> s.servicePk == pk)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Service refreshRelations(Service obj) {
    return new Service(
        obj.pk,
        obj.name,
        obj.duration,
        obj.cost,
        obj.maxSubscribers,
        obj.accountPk,
        new ArrayList<>(subscriptionStore.getAllWhere(s -> s.servicePk == obj.pk)));
  }

  @Override
  public Collection<Service> loadAll() {
    Collection<Service> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT pk, name, duration, cost, max_subscribers, account_pk FROM service");

      ResultSet results = statement.executeQuery();
      while (results.next()) {
        int pk = results.getInt("pk");
        objects.add(
            new Service(
                pk,
                results.getString("name"),
                results.getDouble("duration"),
                results.getDouble("cost"),
                results.getInt("max_subscribers"),
                results.getInt("account_pk"),
                new ArrayList<>(subscriptionStore.getAllWhere(s -> s.servicePk == pk))));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
