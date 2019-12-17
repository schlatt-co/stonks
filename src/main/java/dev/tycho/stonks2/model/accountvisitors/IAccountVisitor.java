package dev.tycho.stonks2.model.accountvisitors;

import dev.tycho.stonks2.model.core.CompanyAccount;
import dev.tycho.stonks2.model.core.HoldingsAccount;

public interface IAccountVisitor {
  void visit(CompanyAccount a);

  void visit(HoldingsAccount a);
}
