package dev.tycho.stonks.Database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.ChatColor;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CompanyDaoImpl extends BaseDaoImpl<Company, UUID> implements CompanyDao{
    public CompanyDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Company.class);
    }

    @Override
    public boolean companyExists(String name) throws SQLException {
        QueryBuilder<Company, UUID> queryBuilder = queryBuilder();
        List list;
        queryBuilder.where().eq("name", name);
        list = queryBuilder.query();
        return list.size() > 0;
    }

    @Override
    public Company getCompany(String name) throws SQLException {
        List<Company> companyList = queryForEq("name", ChatColor.stripColor(name));
        if (companyList.size() == 0) return null;
        return companyList.get(0);
    }

}
