package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.database.Store;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class HoldingsAccountDBI extends JavaSqlDBI<HoldingsAccount> {

  private final Store<Transaction> transactionStore;
  private final Store<Service> serviceStore;
  private final Store<Holding> holdingStore;

  public HoldingsAccountDBI(Connection connection, Store<Transaction> transactionStore, Store<Service> serviceStore, Store<Holding> holdingStore) {
    super(connection);
    this.serviceStore = serviceStore;
    this.transactionStore = transactionStore;
    this.holdingStore = holdingStore;
  }

  @Override
  protected boolean createTable() {
    try {
      connection.createStatement().executeUpdate(
          "CREATE TABLE IF NOT EXISTS holdings_account (" +
              " pk int(11) NOT NULL AUTO_INCREMENT," +
              " name varchar(255) DEFAULT NULL," +
              " uuid varchar(36) DEFAULT NULL," +
              " company_pk int(11) DEFAULT NULL," +
              " profit_account BIT NOT DEFAULT NULL," +
              " PRIMARY KEY (pk) ) "
      );
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public HoldingsAccount create(HoldingsAccount obj) {
    PreparedStatement statement;
    try {
      statement = connection.prepareStatement(
          "INSERT INTO holdings_account (pk, name, uuid, company_pk, profit_account) VALUES (?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      int newPk = Repo.getInstance().getNextAccountPk();
      if (newPk < 0) return null;
      statement.setInt(1, newPk);
      statement.setString(2, obj.name);
      statement.setString(3, uuidToStr(obj.uuid));
      statement.setInt(4, obj.companyPk);
      statement.setBoolean(5, obj.profitAccount);
      statement.executeUpdate();
      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        int createdPk = rs.getInt(1);
        return new HoldingsAccount(createdPk, obj.name, obj.uuid, obj.companyPk, obj.profitAccount, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
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
      statement = connection.prepareStatement(
          "UPDATE holdings_account SET name = ?, uuid = ?, company_pk = ?, profit_account = ? WHERE pk = ?");
      statement.setString(1, obj.name);
      statement.setString(2, uuidToStr(obj.uuid));
      statement.setInt(3, obj.companyPk);
      statement.setBoolean(4, obj.profitAccount);
      statement.setInt(5, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public HoldingsAccount load(int pk) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT name, uuid, company_pk, profit_account FROM holdings_account WHERE pk = ?");
      statement.setInt(1, pk);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        return new HoldingsAccount(
            pk,
            results.getString("name"),
            uuidFromString(results.getString("uuid")),
            results.getInt("company_pk"),
            results.getBoolean("profit_account"),
            new ArrayList<>(transactionStore.getAllWhere(t -> t.accountPk == pk)),
            new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == pk)),
            new ArrayList<>(holdingStore.getAllWhere(h -> h.accountPk == pk)));
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
        obj.profitAccount,
        new ArrayList<>(transactionStore.getAllWhere(t -> t.accountPk == obj.pk)),
        new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == obj.pk)),
        new ArrayList<>(holdingStore.getAllWhere(h -> h.accountPk == obj.pk)));
  }

  @Override
  public Collection<HoldingsAccount> loadAll() {
    Collection<HoldingsAccount> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
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
                results.getBoolean("profit_account"),
                new ArrayList<>(transactionStore.getAllWhere(t -> t.accountPk == pk)),
                new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == pk)),
                new ArrayList<>(holdingStore.getAllWhere(h -> h.accountPk == pk))));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
