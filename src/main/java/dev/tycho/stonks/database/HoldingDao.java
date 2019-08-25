package dev.tycho.stonks.database;

import com.j256.ormlite.dao.Dao;
import dev.tycho.stonks.model.Holding;
import dev.tycho.stonks.model.Member;

public interface HoldingDao extends Dao<Holding, Integer> {
    boolean memberHasHoldings(Member member);
}
