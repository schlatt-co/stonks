package dev.tycho.stonks.model.meta;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.logging.Transaction;
import org.apache.commons.dbcp2.BasicDataSource;
import uk.tsarcasm.tsorm.JavaSqlDBI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class TransactionStore {
  private BasicDataSource dataSource;
  private JavaSqlDBI<Transaction> dbi;

  public TransactionStore(BasicDataSource dataSource, JavaSqlDBI<Transaction> dbi) {
    this.dataSource = dataSource;
    this.dbi = dbi;
  }

  public void insert(Transaction obj) {
    if (obj.pk != 0) {
      throw new IllegalArgumentException("Entity to create already has a primary key");
    }
    new Thread(() -> {
      Transaction created = dbi.insert(obj);
      if (created == null || created.pk == 0) {
        System.out.println("Error creating object");
      }
    }).start();
  }

  public ImmutableCollection<Transaction> getTransactionsForAccount(Account account) {
    Collection<Transaction> objects = new ArrayList<>();
    try {
      try (Connection conn = dataSource.getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT pk, account_pk, payee_uuid, message, amount, timestamp FROM transaction WHERE account_pk = ?");
        statement.setInt(1, account.pk);
        ResultSet results = statement.executeQuery();
        while (results.next()) {
          int pk = results.getInt("pk");
          String uuid = results.getString("payee_uuid");
          UUID u = uuid == null ? null : UUID.fromString(uuid);
          objects.add(new Transaction(
              pk,
              results.getInt("account_pk"),
              u,
              results.getString("message"),
              results.getDouble("amount"),
              results.getTimestamp("timestamp")));
        }
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
