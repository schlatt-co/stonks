package nl.tychovi.stonks.model;

public interface IAccountVisitor {
  void visit(HoldingsAccount a);

  void visit(CompanyAccount a);

  Object result();
}
