package dev.tycho.stonks.Database.accountvisitors;

import dev.tycho.stonks.Database.CompanyAccount;
import dev.tycho.stonks.Database.IAccountVisitor;

public abstract class ReturningAccountVisitor implements IAccountVisitor {
    protected Object val;

    public Object getRecentVal() {
        return val;
    }
    @Override
    public void visit(CompanyAccount a) {

    }
}
