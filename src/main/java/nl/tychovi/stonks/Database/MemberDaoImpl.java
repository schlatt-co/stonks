package nl.tychovi.stonks.Database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MemberDaoImpl extends BaseDaoImpl<Member, UUID> implements MemberDao{
    public MemberDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Member.class);
    }

    @Override
    public List<Member> getInvites(Player player) throws SQLException {
        QueryBuilder<Member, UUID> queryBuilder = queryBuilder();
        List<Member> list;
        queryBuilder.where().eq("acceptedInvite", "0").and().eq("uuid", player.getUniqueId());
        list = queryBuilder.query();
        if(list.size() > 0) {
            return list;
        }
        return null;
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
}
