package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.store.OneToMany;

public class HoldingToAccountOTM implements OneToMany<HoldingsAccount, Holding> {
  @Override
  public int getParentPk(Holding child) {
    return child.getAccountPk();
  }
}
