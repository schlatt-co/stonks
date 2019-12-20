package dev.tycho.stonks.database;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.logging.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class TransactionStore {
  private Connection connection;
  private JavaSqlDBI<Transaction> dbi;

  public TransactionStore(Connection connection, JavaSqlDBI<Transaction> dbi) {
    this.connection = connection;
    this.dbi = dbi;
  }

  public void create(Transaction obj) {
    if (obj.pk != 0) {
      throw new IllegalArgumentException("Entity to create already has a primary key");
    }
    new Thread(() -> {
      Transaction created = dbi.create(obj);
      if (created == null || created.pk == 0) {
        System.out.println("Error creating object");
      }
    }).start();
  }

  public ImmutableCollection<Transaction> getTransactionsForAccount(Account account) {
    Collection<Transaction> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT pk, account_pk, payee_uuid, message, amount, timestamp FROM transaction WHERE account_pk = ?");
      statement.setInt(1, account.pk);
      ResultSet results = statement.executeQuery();
      while (results.next()) {
        int pk = results.getInt("pk");
        objects.add(new Transaction(
            pk,
            results.getInt("account_pk"),
            JavaSqlDBI.uuidFromString(results.getString("payee_uuid")),
            results.getString("message"),
            results.getDouble("amount"),
            results.getTimestamp("timestamp")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ImmutableList.copyOf(objects);
  }

  public ImmutableCollection<Transaction> getTransactionsForAccountTimeLimited(Account account, double numDays) {
    Collection<Transaction> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT pk, account_pk, payee_uuid, message, amount, timestamp FROM transaction " +
              "WHERE account_pk = ? AND DATE(timestamp) = CURDATE() - INTERVAL ? DAY  ");
      statement.setInt(1, account.pk);
      statement.setDouble(2, numDays);
      ResultSet results = statement.executeQuery();
      while (results.next()) {
        int pk = results.getInt("pk");
        objects.add(new Transaction(
            pk,
            results.getInt("account_pk"),
            JavaSqlDBI.uuidFromString(results.getString("payee_uuid")),
            results.getString("message"),
            results.getDouble("amount"),
            results.getTimestamp("timestamp")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ImmutableList.copyOf(objects);
  }


  public void createTable() {
    dbi.createTable();
  }
}
