package dev.tycho.stonks.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.core.HoldingsAccount;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HoldingsAccountDaoImpl extends BaseDaoImpl<HoldingsAccount, Integer> {
  public HoldingsAccountDaoImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, HoldingsAccount.class);
  }

  public List<HoldingsAccount> playerHoldingsAccounts(Player player) {
    try {
      QueryBuilder<Holding, Integer> holdingQuery = DatabaseHelper.getInstance().getDatabaseManager().getHoldingDao().queryBuilder();
      QueryBuilder<HoldingsAccount, Integer> accountQuery = queryBuilder();
      holdingQuery.where().eq("player", player.getUniqueId());
      accountQuery.join(holdingQuery);
      return accountQuery.query();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

}
