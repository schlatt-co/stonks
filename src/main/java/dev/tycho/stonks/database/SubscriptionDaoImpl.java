package dev.tycho.stonks.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SubscriptionDaoImpl extends BaseDaoImpl<Subscription, Integer> {
  public SubscriptionDaoImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, Subscription.class);
  }

  public Collection<Subscription> getPlayerSubscriptions(Player player) {
    QueryBuilder<Subscription, Integer> queryBuilder = queryBuilder();
    try {
      queryBuilder.where().eq("playerId", player.getUniqueId());
      Collection<Subscription> c = queryBuilder.query();
      if (c == null) c = new ArrayList<>();
      return c;
    } catch (SQLException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }

  }


}
