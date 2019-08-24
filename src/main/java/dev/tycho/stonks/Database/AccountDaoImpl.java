package dev.tycho.stonks.Database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class AccountDaoImpl extends BaseDaoImpl<CompanyAccount, Integer> {
    public AccountDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CompanyAccount.class);
    }

    public double getBalance(Account account) {
        return account.getTotalBalance();
    }


}