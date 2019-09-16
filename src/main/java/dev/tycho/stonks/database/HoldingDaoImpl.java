package dev.tycho.stonks.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.core.Member;

import java.sql.SQLException;

public class HoldingDaoImpl extends BaseDaoImpl<Holding, Integer> implements HoldingDao {
  public HoldingDaoImpl(ConnectionSource connectionSource) throws SQLException {
    super(connectionSource, Holding.class);
  }

  @Override
  public boolean memberHasHoldings(Member member) {
    //todo someone should fix this
    return false;
  }


}
