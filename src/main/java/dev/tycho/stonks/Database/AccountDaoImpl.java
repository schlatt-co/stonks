package dev.tycho.stonks.Database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class AccountDaoImpl extends BaseDaoImpl<CompanyAccount, Integer> {
    public AccountDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CompanyAccount.class);
    }

    public double getBalance(Account account) {
        AccountBalanceVisitor visitor = new AccountBalanceVisitor();
        account.accept(visitor);
        return visitor.getLastValue();
    }



    private class AccountBalanceVisitor implements IAccountVisitor {
        private double lastValue = 0;
        public double getLastValue() {
            return lastValue;
        }

        @Override
        public void Visit(CompanyAccount a) {
            lastValue = a.getBalance();
        }
    }

}