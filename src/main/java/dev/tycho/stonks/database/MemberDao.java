package dev.tycho.stonks.database;

import com.j256.ormlite.dao.Dao;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.core.Role;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface MemberDao extends Dao<Member, UUID> {
    List<Member> getInvites(Player player);

  void handleInvite(Boolean response, UUID companyUuid, UUID playerUuid) throws SQLException;

  Member getMember(Player player, Company company) throws SQLException;

  void deleteMember(Member member) throws SQLException;

  void setRole(Member member, Role role) throws SQLException;
}
