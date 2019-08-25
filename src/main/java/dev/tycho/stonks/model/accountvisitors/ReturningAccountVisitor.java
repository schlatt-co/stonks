package dev.tycho.stonks.model.accountvisitors;

import dev.tycho.stonks.model.CompanyAccount;

public abstract class ReturningAccountVisitor implements IAccountVisitor {
    protected Object val;

    public Object getRecentVal() {
        return val;
    }
    @Override
    public void visit(CompanyAccount a) {

    }
}
