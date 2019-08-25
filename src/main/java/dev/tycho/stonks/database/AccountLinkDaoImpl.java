package dev.tycho.stonks.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import dev.tycho.stonks.model.*;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountLinkDaoImpl extends BaseDaoImpl<AccountLink, Integer> {
    public AccountLinkDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, AccountLink.class);
    }

    public Company getCompany(Account account) {
        QueryBuilder<AccountLink, Integer> queryBuilder = queryBuilder();
        try {
            queryBuilder.where().eq("account_id", account.getId());
            AccountLink ao = queryBuilder.queryForFirst();
            if (ao != null) {
                return ao.getCompany();
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Account> getAccounts(Company company) {
        QueryBuilder<AccountLink, Integer> queryBuilder = queryBuilder();
        try {
            queryBuilder.where().eq("company_id", company.getId());
            List<AccountLink> accountLinks = queryBuilder.query();
            List<Account> accounts = new ArrayList<>();
            accountLinks.forEach((ao)->accounts.add(ao.getAccount()));
            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }




    public List<CompanyAccount> getCompanyAccounts(Company company) {
        QueryBuilder<AccountLink, Integer> queryBuilder = queryBuilder();
        try {
            queryBuilder.where().eq("company_id", company.getId());
            List<AccountLink> accountLinks = queryBuilder.query();
            List<CompanyAccount> accounts = new ArrayList<>();
            accountLinks.forEach((ao)-> {
                if (ao.getAccountType() == AccountType.CompanyAccount)
                    accounts.add((CompanyAccount)ao.getAccount());
                    });
            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountLink getAccountLink(Account account) {
        QueryBuilder<AccountLink, Integer> queryBuilder = queryBuilder();
        try {
            ReturningAccountVisitor v = new ReturningAccountVisitor() {
                @Override
                public void visit(CompanyAccount a) {
                    val = "companyAccount_id";
                }

                @Override
                public void visit(HoldingsAccount a) {
                    val = "holdingsAccount_id";
                }
            };
            account.accept(v);
            System.out.println((String)v.getRecentVal());
            queryBuilder.where().eq((String)v.getRecentVal(), account.getId());
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}