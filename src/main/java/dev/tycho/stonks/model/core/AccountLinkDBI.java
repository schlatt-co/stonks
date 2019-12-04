package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.store_old.JavaSqlDBI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class AccountLinkDBI extends JavaSqlDBI<AccountLink> {
  public AccountLinkDBI(Connection connection) {
    super(connection);
  }

  @Override
  protected void createTable() throws SQLException {
    connection.createStatement().executeQuery(
        "CREATE TABLE 'accountlink' (" +
            " 'pk' int(11) NOT NULL," +
            " 'company_pk' int(11) NOT NULL," +
            " 'account_pk' int(11) NOT NULL," +
            " 'account_type' varchar(255) NOT NULL," +
            " PRIMARY KEY ('pk') ) "
    );
  }

  @Override
  public int create(AccountLink obj) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(
        "INSERT INTO accountlink (company_pk, account_pk, account_type) VALUES (?, ?, ?)");
    statement.setInt(1, obj.getCompanyPk());
    statement.setInt(2, obj.getAccountPk());
    statement.setString(3, obj.getAccountType());
    statement.execute();
    return 0; //todo get primary key of created row
  }

  @Override
  public void save(AccountLink obj) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(
        "UPDATE accountlink SET (company_pk, account_pk, account_type) VALUES (?, ?, ?) WHERE pk = ?");
    statement.setInt(1, obj.getCompanyPk());
    statement.setInt(2, obj.getAccountPk());
    statement.setString(3, obj.getAccountType());
    statement.setInt(4, obj.getPk());
    statement.execute();
  }

  @Override
  public AccountLink load(int pk) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(
        "SELECT company_pk, account_pk, account_type FROM accountlink WHERE pk = ?");
    ResultSet results = statement.executeQuery();
    if (results.next()) {
      return new AccountLink(
          results.getInt("company_pk"),
          results.getInt("account_pk"),
          results.getString("account_type"));
    }
    return null;
  }

  @Override
  public Collection<AccountLink> loadAll() throws SQLException {
    Collection<AccountLink> objects = new ArrayList<>();
    PreparedStatement statement = connection.prepareStatement(
        "SELECT company_pk, account_pk, account_type FROM accountlink WHERE pk = ?");
    ResultSet results = statement.executeQuery();
    while (results.next()) {
      objects.add(new AccountLink(
          results.getInt("company_pk"),
          results.getInt("account_pk"),
          results.getString("account_type")));
    }
    return objects;
  }
}
