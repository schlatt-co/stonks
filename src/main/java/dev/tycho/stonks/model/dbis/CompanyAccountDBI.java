package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.database.Store;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.CompanyAccount;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class CompanyAccountDBI extends JavaSqlDBI<CompanyAccount> {

  private final Store<Transaction> transactionStore;
  private final Store<Service> serviceStore;

  public CompanyAccountDBI(Connection connection, Store<Transaction> transactionStore, Store<Service> serviceStore) {
    super(connection);
    this.serviceStore = serviceStore;
    this.transactionStore = transactionStore;
  }

  @Override
  protected boolean createTable() {
    try {
      connection.createStatement().executeUpdate(
          "CREATE TABLE IF NOT EXISTS company_account (" +
              " pk int(11) NOT NULL AUTO_INCREMENT," +
              " name varchar(255) DEFAULT NULL," +
              " uuid varchar(36) DEFAULT NULL," +
              " company_pk int(11) DEFAULT NULL," +
              " balance double NOT NULL DEFAULT 0," +
              " PRIMARY KEY (pk) ) "
      );
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public CompanyAccount create(CompanyAccount obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "INSERT INTO company_account (pk, name, uuid, company_pk, balance) VALUES (?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      int newPk = Repo.getInstance().getNextAccountPk();
      if (newPk < 0) return null;
      statement.setInt(1, newPk);
      statement.setString(2, obj.name);
      statement.setString(3, obj.uuid.toString());
      statement.setInt(4, obj.companyPk);
      statement.setDouble(5, obj.balance);
      statement.executeUpdate();
      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        int createdPk = rs.getInt(1);
        return new CompanyAccount(createdPk, obj.name, obj.uuid, obj.companyPk, new ArrayList<>(), new ArrayList<>(), obj.balance);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(CompanyAccount obj) {
    //Deleting accounts is not supported at this time
    return false;
  }

  @Override
  public boolean save(CompanyAccount obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "UPDATE company_account SET name = ?, uuid = ?, company_pk = ?, balance = ? WHERE pk = ?");
      statement.setString(1, obj.name);
      statement.setString(2, obj.uuid.toString());
      statement.setInt(3, obj.companyPk);
      statement.setDouble(4, obj.balance);
      statement.setInt(5, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public CompanyAccount load(int pk) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT name, uuid, company_pk, balance FROM company_account WHERE pk = ?");
      statement.setInt(1, pk);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        return new CompanyAccount(
            pk,
            results.getString("name"),
            UUID.fromString(results.getString("uuid")),
            results.getInt("company_pk"),
            new ArrayList<>(transactionStore.getAllWhere(t -> t.accountPk == pk)),
            new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == pk)),
            results.getDouble("balance"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public CompanyAccount refreshRelations(CompanyAccount obj) {
    return new CompanyAccount(
        obj.pk,
        obj.name,
        obj.uuid,
        obj.companyPk,
        new ArrayList<>(transactionStore.getAllWhere(t -> t.accountPk == obj.pk)),
        new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == obj.pk)),
        obj.balance);
  }

  @Override
  public Collection<CompanyAccount> loadAll() {
    Collection<CompanyAccount> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT pk, name, uuid, company_pk, balance FROM company_account");

      ResultSet results = statement.executeQuery();
      while (results.next()) {
        int pk = results.getInt("pk");
        objects.add(
            new CompanyAccount(
                pk,
                results.getString("name"),
                UUID.fromString(results.getString("uuid")),
                results.getInt("company_pk"),
                new ArrayList<>(transactionStore.getAllWhere(t -> t.accountPk == pk)),
                new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == pk)),
                results.getDouble("balance")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }

}
