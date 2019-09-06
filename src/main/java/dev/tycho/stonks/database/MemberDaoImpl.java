package dev.tycho.stonks.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.core.Role;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemberDaoImpl extends BaseDaoImpl<Member, UUID> implements MemberDao{
    public MemberDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Member.class);
    }

    @Override
    public List<Member> getInvites(Player player) {
        QueryBuilder<Member, UUID> queryBuilder = queryBuilder();
        List<Member> list;

        try {
            queryBuilder.where().eq("acceptedInvite", "0").and().eq("uuid", player.getUniqueId());
            list = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        if(list.size() > 0) {
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public void handleInvite(Boolean response, UUID companyUuid, UUID playerUuid) throws SQLException {
        if(response) {
            UpdateBuilder<Member, UUID> updateBuilder = updateBuilder();
            updateBuilder.where()
                    .eq("acceptedInvite", "0")
                    .and()
                    .eq("uuid", playerUuid)
                    .and()
                    .eq("company_id", companyUuid);
            updateBuilder.updateColumnValue("acceptedInvite", true);
            updateBuilder.update();
        } else {
            DeleteBuilder<Member, UUID> deleteBuilder = deleteBuilder();
            deleteBuilder.where()
                    .eq("acceptedInvite", "0")
                    .and()
                    .eq("uuid", playerUuid)
                    .and()
                    .eq("company_id", companyUuid);
            deleteBuilder.delete();
        }
    }

    @Override
    public Member getMember(Player player, Company company) throws SQLException {
        QueryBuilder<Member, UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq("uuid", player.getUniqueId()).and().eq("company_id", company.getId());
        List<Member> list = queryBuilder.query();
        if(!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void deleteMember(Member member) throws SQLException {
        DeleteBuilder<Member, UUID> deleteBuilder = deleteBuilder();
        deleteBuilder.where().eq("uuid", member.getUuid()).and().eq("company_id", member.getCompany().getId());
        deleteBuilder.delete();
    }

    @Override
    public void setRole(Member member, Role role) throws SQLException {
        UpdateBuilder<Member, UUID> updateBuilder = updateBuilder();
        updateBuilder.where().eq("uuid", member.getUuid()).and().eq("company_id", member.getCompany().getId());
        updateBuilder.updateColumnValue("role", role);
        updateBuilder.update();
    }
}
