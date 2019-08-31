package dev.tycho.stonks.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import dev.tycho.stonks.model.Company;
import dev.tycho.stonks.model.Member;
import dev.tycho.stonks.model.Role;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompanyDaoImpl extends BaseDaoImpl<Company, UUID> implements CompanyDao {
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

    public List<Company> getAllCompanies() {
        QueryBuilder<Company, UUID> queryBuilder = queryBuilder();
        queryBuilder.orderBy("name", true);
        try {
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Company> getAllCompaniesWhereManager(Player player, QueryBuilder<Member, UUID> memberQuery) {

        Where<Member, UUID> where = memberQuery.where();
        try {
            where.and(
                    where.or(
                            where.eq("role", Role.CEO),
                            where.eq("role", Role.CEO)),
                    where.eq("uuid", player.getUniqueId()));

            QueryBuilder<Company, UUID> companyQuery = queryBuilder();
            // join with the order query
            return companyQuery.join(memberQuery).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
