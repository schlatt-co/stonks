package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.store.JavaSqlDBI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class CompanyDBI extends JavaSqlDBI<Company> {

  public CompanyDBI(Connection connection) {
    super(connection);
  }


  @Override
  protected void createTable() throws SQLException {
    connection.createStatement().executeQuery(
        "CREATE TABLE 'company' (" +
            " 'pk' int(11) NOT NULL," +
            " 'name' varchar(255) DEFAULT NULL," +
            " 'shopName' varchar(255) DEFAULT NULL," +
            " 'logoMaterial' varchar(255) DEFAULT NULL," +
            " 'verified' bit NOT NULL DEFAULT '0'," +
            " 'hidden' bit NOT NULL DEFAULT '0'," +
            " PRIMARY KEY ('id') ) "
    );
  }

  @Override
  public int create(Company obj) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(
        "INSERT INTO company (name, shopName, logoMaterial, verified, hidden) VALUES (?, ?, ?, ?, ?)");
    statement.setString(1, obj.getName());
    statement.setString(2, obj.getShopName());
    statement.setString(3, obj.getLogoMaterial());
    statement.setBoolean(4, obj.isVerified());
    statement.setBoolean(5, obj.isHidden());
    statement.execute();
    return 0; //todo get primary key of created row
  }

  @Override
  public void save(Company obj) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(
        "UPDATE company SET (name, shopName, logoMaterial, verified, hidden) VALUES (?, ?, ?, ?, ?) WHERE pk = ?");
    statement.setString(1, obj.getName());
    statement.setString(2, obj.getShopName());
    statement.setString(3, obj.getLogoMaterial());
    statement.setBoolean(4, obj.isVerified());
    statement.setBoolean(5, obj.isHidden());
    statement.setInt(6, obj.getPk());
    statement.execute();
  }

  @Override
  public Company load(int pk) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(
        "SELECT name, shopName, logoMaterial, verified, hidden FROM company WHERE pk = ?");
    ResultSet results = statement.executeQuery();
    if (results.next()) {
      Company company = new Company(results.getString("name"),
          results.getString("shopName"),
          results.getString("logoMaterial"),
          results.getBoolean("verified"),
          results.getBoolean("hidden"));
    }
    return null;
  }

  @Override
  public Collection<Company> loadAll() throws SQLException {
    Collection<Company> objects = new ArrayList<>();
    PreparedStatement statement = connection.prepareStatement(
        "SELECT name, shopName, logoMaterial, verified, hidden FROM company WHERE pk = ?");
    ResultSet results = statement.executeQuery();
    while (results.next()) {
      Company company = new Company(results.getString("name"),
          results.getString("shopName"),
          results.getString("logoMaterial"),
          results.getBoolean("verified"),
          results.getBoolean("hidden"));
      objects.add(company);
    }
    return objects;
  }
}
