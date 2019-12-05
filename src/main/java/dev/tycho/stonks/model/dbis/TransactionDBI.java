package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.db_new.JavaSqlDBI;
import dev.tycho.stonks.model.logging.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class TransactionDBI extends JavaSqlDBI<Transaction> {


  public TransactionDBI(Connection connection) {
    super(connection);
  }

  @Override
  protected boolean createTable() {
    try {
      connection.createStatement().executeUpdate(
          "CREATE TABLE IF NOT EXISTS transaction(" +
              "    pk INT(11) NOT NULL AUTO_INCREMENT," +
              "    account_pk INT(11) DEFAULT NULL," +
              "    payee_uuid VARCHAR(54) DEFAULT NULL," +
              "    message VARCHAR(255)," +
              "    amount double DEFAULT NULL," +
              "    timestamp datetime DEFAULT NULL," +
              "    PRIMARY KEY(pk))"
      );
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Transaction create(Transaction obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "INSERT INTO transaction (account_pk, payee_uuid, message, amount, timestamp) VALUES (?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      statement.setInt(1, obj.accountPk);
      statement.setString(2, obj.payeeUUID.toString());
      statement.setString(3, obj.message);
      statement.setDouble(4, obj.amount);
      statement.setDate(5, obj.timestamp);
      statement.executeUpdate();
      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        int newPk = rs.getInt(1);
        return new Transaction(newPk, obj.accountPk, obj.payeeUUID, obj.message, obj.amount, obj.timestamp);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Transaction obj) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "DELETE FROM transaction WHERE pk = ?");
      statement.setInt(1, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean save(Transaction obj) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "UPDATE transaction SET account_pk = ?, payee_uuid = ?, message = ?, amount = ?, timestamp = ? WHERE pk = ?");
      statement.setInt(1, obj.accountPk);
      statement.setString(2, obj.payeeUUID.toString());
      statement.setString(3, obj.message);
      statement.setDouble(4, obj.amount);
      statement.setDate(5, obj.timestamp);
      statement.setInt(6, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Transaction load(int pk) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT account_pk, payee_uuid, message, amount, timestamp FROM transaction WHERE pk = ?");
      statement.setInt(1, pk);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        return new Transaction(
            pk,
            results.getInt("account_pk"),
            UUID.fromString(results.getString("payee_uuid")),
            results.getString("message"),
            results.getDouble("amount"),
            results.getDate("timestamp"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Collection<Transaction> loadAll() {
    Collection<Transaction> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT pk, account_pk, payee_uuid, message, amount, timestamp FROM transaction");

      ResultSet results = statement.executeQuery();
      while (results.next()) {
        int pk = results.getInt("pk");
        objects.add(new Transaction(
            pk,
            results.getInt("account_pk"),
            UUID.fromString(results.getString("payee_uuid")),
            results.getString("message"),
            results.getDouble("amount"),
            results.getDate("timestamp")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
