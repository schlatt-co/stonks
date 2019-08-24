package dev.tycho.stonks.Database;

public interface IAccountVisitor {
    void visit(CompanyAccount a);
    void visit(HoldingsAccount a);
}
