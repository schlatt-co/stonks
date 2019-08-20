package nl.tychovi.stonks.Database;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public interface CompanyAccountDao extends Dao<CompanyAccount, Integer> {

    int getCompanyValue(Company company) throws SQLException;
}
