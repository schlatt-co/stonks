package dev.tycho.stonks.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import dev.tycho.stonks.model.CompanyAccount;

import java.sql.SQLException;

public class CompanyAccountDaoImpl extends BaseDaoImpl<CompanyAccount, Integer> implements CompanyAccountDao {
    public CompanyAccountDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CompanyAccount.class);
    }


}
