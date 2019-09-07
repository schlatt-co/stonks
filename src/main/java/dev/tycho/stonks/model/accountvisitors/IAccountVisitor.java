package dev.tycho.stonks.model.accountvisitors;

import dev.tycho.stonks.model.core.CompanyAccount;
import dev.tycho.stonks.model.core.HoldingsAccount;

public interface IAccountVisitor {
  void visit(CompanyAccount a);

  void visit(HoldingsAccount a);
}
