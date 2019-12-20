package dev.tycho.stonks.model.trading;

import dev.tycho.stonks.database.Entity;
import dev.tycho.stonks.database.TransactionStore;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.logging.Transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

public class Stock extends Entity {
  public final int companyPk;
  public final int commissionAccountPk;
  public final String tickerName;
  public final Timestamp ipoDate;
  public final int totalShares;
  public final boolean locked;
  public final Collection<Shareholder> shareholders;

  public Stock(int pk, int companyPk, int commissionAccountPk, String tickerName, Timestamp ipoDate,
               int totalShares, boolean locked, Collection<Shareholder> shareholders) {
    super(pk);
    this.companyPk = companyPk;
    this.commissionAccountPk = commissionAccountPk;
    this.tickerName = tickerName;
    this.ipoDate = ipoDate;
    this.totalShares = totalShares;
    this.locked = locked;
    this.shareholders = shareholders;
  }

  public Stock(Stock s) {
    super(s.pk);
    this.companyPk = s.companyPk;
    this.commissionAccountPk = s.commissionAccountPk;
    this.tickerName = s.tickerName;
    this.ipoDate = s.ipoDate;
    this.totalShares = s.totalShares;
    this.locked = s.locked;
    this.shareholders = new ArrayList<>(s.shareholders);
  }

  public static double value(Stock stock, Company company, TransactionStore transactions) {
    if (stock.companyPk != company.pk) throw new IllegalArgumentException("The stock and company relation is invalid");
    double totalProfit = 0;
    for (Account account : company.accounts) {
      //If the account is a profit account
      if (account.profitAccount) {
        //Get all transactions that are +ve and less than a week old
        //Add the value of the transaction to total profit
        for (Transaction transaction : transactions.getTransactionsForAccountTimeLimited(account, 7)) {
          if (transaction.amount > 0) {
            totalProfit += transaction.amount;
          }
        }
      }
    }
    return totalProfit;
  }

}
