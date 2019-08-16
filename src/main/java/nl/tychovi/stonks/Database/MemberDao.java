package nl.tychovi.stonks.Database;

import com.j256.ormlite.dao.Dao;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface MemberDao extends Dao<Member, UUID> {
    List<Member> getInvites(Player player) throws SQLException;
}
