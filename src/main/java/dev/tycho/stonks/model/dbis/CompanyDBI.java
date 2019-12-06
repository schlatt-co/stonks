package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.database.Store;
import dev.tycho.stonks.model.core.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class CompanyDBI extends JavaSqlDBI<Company> {

  private final Store<Member> memberStore;
  private final Store<CompanyAccount> companyAccountStore;
  private final Store<HoldingsAccount> holdingsAccountStore;


  public CompanyDBI(Connection connection, Store<Member> memberStore, Store<CompanyAccount> companyAccountStore, Store<HoldingsAccount> holdingsAccountStore) {
    super(connection);
    this.memberStore = memberStore;
    this.companyAccountStore = companyAccountStore;
    this.holdingsAccountStore = holdingsAccountStore;
  }

  @Override
  protected boolean createTable() {
    try {
      connection.createStatement().executeUpdate(
          "CREATE TABLE IF NOT EXISTS company (" +
              " pk int(11) NOT NULL AUTO_INCREMENT," +
              " name varchar(255) DEFAULT NULL," +
              " shopName varchar(255) DEFAULT NULL," +
              " logoMaterial varchar(255) DEFAULT NULL," +
              " verified bit NOT NULL DEFAULT 0," +
              " hidden bit NOT NULL DEFAULT 0," +
              " PRIMARY KEY (pk) ) "
      );
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Company create(Company obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "INSERT INTO company (name, shopName, logoMaterial, verified, hidden) VALUES (?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, obj.name);
      statement.setString(2, obj.shopName);
      statement.setString(3, obj.logoMaterial);
      statement.setBoolean(4, obj.verified);
      statement.setBoolean(5, obj.hidden);
      statement.executeUpdate();
      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        int newPk = rs.getInt(1);
        return new Company(newPk, obj.name, obj.shopName, obj.logoMaterial, obj.hidden, obj.verified, new ArrayList<>(), new ArrayList<>()
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Company obj) {
    //We dont support deleting companies at this time
    return false;
  }

  @Override
  public boolean save(Company obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "UPDATE company SET name = ?, shopName = ?, logoMaterial = ?, verified = ?, hidden = ? WHERE pk = ?");
      statement.setString(1, obj.name);
      statement.setString(2, obj.shopName);
      statement.setString(3, obj.logoMaterial);
      statement.setBoolean(4, obj.verified);
      statement.setBoolean(5, obj.hidden);
      statement.setInt(6, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Company load(int pk) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT name, shopName, logoMaterial, verified, hidden FROM company WHERE pk = ?");
      statement.setInt(1, pk);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        //Get all accounts
        Collection<Account> accounts = new ArrayList<>();
        accounts.addAll(companyAccountStore.getAllWhere(a -> a.companyPk == pk));
        accounts.addAll(holdingsAccountStore.getAllWhere(a -> a.companyPk == pk));

        Company company = new Company(
            pk,
            results.getString("name"),
            results.getString("shopName"),
            results.getString("logoMaterial"),
            results.getBoolean("verified"),
            results.getBoolean("hidden"),
            accounts, new ArrayList<>(memberStore.getAllWhere(m -> m.companyPk == pk))
        );
        return company;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Collection<Company> loadAll() {
    Collection<Company> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT pk, name, shopName, logoMaterial, verified, hidden FROM company");

      ResultSet results = statement.executeQuery();
      while (results.next()) {
        int pk = results.getInt("pk");
        //Get all accounts
        Collection<Account> accounts = new ArrayList<>();
        accounts.addAll(companyAccountStore.getAllWhere(a -> a.companyPk == pk));
        accounts.addAll(holdingsAccountStore.getAllWhere(a -> a.companyPk == pk));
        objects.add(
            new Company(
                pk,
                results.getString("name"),
                results.getString("shopName"),
                results.getString("logoMaterial"),
                results.getBoolean("verified"),
                results.getBoolean("hidden"),
                accounts,
                new ArrayList<>(memberStore.getAllWhere(m -> m.companyPk == pk))
            ));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
