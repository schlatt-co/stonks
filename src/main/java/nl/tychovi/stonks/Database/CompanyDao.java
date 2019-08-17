package nl.tychovi.stonks.Database;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.UUID;

public interface CompanyDao extends Dao<Company, UUID> {
    boolean companyExists(String name) throws SQLException;

    Company getCompany(String name) throws SQLException;
}
