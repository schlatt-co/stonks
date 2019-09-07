package dev.tycho.stonks.model.accountvisitors;

import dev.tycho.stonks.model.CompanyAccount;
import dev.tycho.stonks.model.HoldingsAccount;

public interface IAccountVisitor {
  void visit(CompanyAccount a);

  void visit(HoldingsAccount a);
}
