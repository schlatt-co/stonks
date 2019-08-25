package dev.tycho.stonks.Database;

import com.j256.ormlite.dao.Dao;

public interface HoldingDao extends Dao<Holding, Integer> {
    boolean memberHasHoldings(Member member);
}
