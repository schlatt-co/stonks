package dev.tycho.stonks.Database;

import com.j256.ormlite.dao.Dao;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface MemberDao extends Dao<Member, UUID> {
    List<Member> getInvites(Player player) throws SQLException;

    void handleInvite(Boolean response, UUID companyUuid, UUID playerUuid) throws SQLException;

    Member getMember(Player player, Company company) throws SQLException;

    void deleteMember(Member member) throws SQLException;
}
