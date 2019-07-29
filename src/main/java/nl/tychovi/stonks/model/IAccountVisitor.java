package nl.tychovi.stonks.model;

public interface IAccountVisitor {
    public void visit(HoldingsAccount a);
    public void visit(CompanyAccount a);
    public Object result();
}
