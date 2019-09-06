package dev.tycho.stonks.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import dev.tycho.stonks.logging.Transaction;
import dev.tycho.stonks.model.AccountLink;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl extends BaseDaoImpl<Transaction, Integer> {
  public TransactionDaoImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, Transaction.class);
  }

  public List<Transaction> getTransactionsForAccount(AccountLink accountLink, QueryBuilder<AccountLink, Integer> accountLinkQuery, long limit, long offset) {
    try {
      accountLinkQuery.where().eq("id", accountLink.getId());
      QueryBuilder<Transaction, Integer> transactionQuery = queryBuilder();
      // join with the order query
      transactionQuery.offset(offset).limit(limit);
      transactionQuery.orderBy("id", false);
      return transactionQuery.join(accountLinkQuery).query();
    } catch (SQLException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  public List<Transaction> getAllTransactionsForAccount(AccountLink accountLink, QueryBuilder<AccountLink, Integer> accountLinkQuery) {
    try {
      accountLinkQuery.where().eq("id", accountLink.getId());
      QueryBuilder<Transaction, Integer> transactionQuery = queryBuilder();
      // join with the order query
      return transactionQuery.join(accountLinkQuery).query();
    } catch (SQLException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
