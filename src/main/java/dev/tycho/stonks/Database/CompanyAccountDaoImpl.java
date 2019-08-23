package dev.tycho.stonks.Database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class CompanyAccountDaoImpl extends BaseDaoImpl<CompanyAccount, Integer> implements CompanyAccountDao {
    public CompanyAccountDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CompanyAccount.class);
    }

    @Override
    public int getCompanyValue(Company company) throws SQLException {
        QueryBuilder<CompanyAccount, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("company_id", company.getId());
        List<CompanyAccount> list = queryBuilder.query();
        int value = 0;
        for(CompanyAccount companyAccount : list) {
            value += companyAccount.getBalance();
        }
        return value;
    }
}
