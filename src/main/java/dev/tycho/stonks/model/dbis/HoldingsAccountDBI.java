package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.database.Store;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.model.service.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class HoldingsAccountDBI extends JavaSqlDBI<HoldingsAccount> {
  private final Store<Service> serviceStore;
  private final Store<Holding> holdingStore;

  public HoldingsAccountDBI(DataSource dataSource, Store<Service> serviceStore, Store<Holding> holdingStore) {
    super(dataSource);
    this.serviceStore = serviceStore;
    this.holdingStore = holdingStore;
  }

  @Override
  protected boolean createTable() {
    try {
      try (Connection conn = getConnection()) {
        conn.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS holdings_account (" +
                " pk int(11) NOT NULL AUTO_INCREMENT," +
                " name varchar(255) DEFAULT NULL," +
                " uuid varchar(36) DEFAULT NULL," +
                " company_pk int(11) DEFAULT NULL," +
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
  public HoldingsAccount create(HoldingsAccount obj) {
    PreparedStatement statement;
    try {
      try (Connection conn = getConnection()) {
        statement = conn.prepareStatement(
            "INSERT INTO holdings_account (pk, name, uuid, company_pk) VALUES (?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS);
        int newPk = Repo.getInstance().getNextAccountPk(conn);
        if (newPk < 0) return null;
        statement.setInt(1, newPk);
        statement.setString(2, obj.name);
        statement.setString(3, uuidToStr(obj.uuid));
        statement.setInt(4, obj.companyPk);
        statement.executeUpdate();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          int createdPk = rs.getInt(1);
          return new HoldingsAccount(createdPk, obj.name, obj.uuid, obj.companyPk, new ArrayList<>(), new ArrayList<>());
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(HoldingsAccount obj) {
    //Deleting accounts is not supported at this time
    return false;
  }

  @Override
  public boolean save(HoldingsAccount obj) {
    PreparedStatement statement;
    try {
      try (Connection conn = getConnection()) {
        statement = conn.prepareStatement(
            "UPDATE holdings_account SET name = ?, uuid = ?, company_pk = ? WHERE pk = ?");
        statement.setString(1, obj.name);
        statement.setString(2, uuidToStr(obj.uuid));
        statement.setInt(3, obj.companyPk);
        statement.setInt(4, obj.pk);
        statement.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public HoldingsAccount load(int pk) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT name, uuid, company_pk FROM holdings_account WHERE pk = ?");
        statement.setInt(1, pk);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
          return new HoldingsAccount(
              pk,
              results.getString("name"),
              uuidFromString(results.getString("uuid")),
              results.getInt("company_pk"),
              new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == pk)),
              new ArrayList<>(holdingStore.getAllWhere(h -> h.accountPk == pk)));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public HoldingsAccount refreshRelations(HoldingsAccount obj) {
    return new HoldingsAccount(
        obj.pk,
        obj.name,
        obj.uuid,
        obj.companyPk,
        new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == obj.pk)),
        new ArrayList<>(holdingStore.getAllWhere(h -> h.accountPk == obj.pk)));
  }

  @Override
  public Collection<HoldingsAccount> loadAll() {
    Collection<HoldingsAccount> objects = new ArrayList<>();
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT pk, name, uuid, company_pk FROM holdings_account");

        ResultSet results = statement.executeQuery();
        while (results.next()) {
          int pk = results.getInt("pk");
          objects.add(
              new HoldingsAccount(
                  pk,
                  results.getString("name"),
                  uuidFromString(results.getString("uuid")),
                  results.getInt("company_pk"),
                  new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == pk)),
                  new ArrayList<>(holdingStore.getAllWhere(h -> h.accountPk == pk))));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
