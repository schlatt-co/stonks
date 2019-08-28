package dev.tycho.stonks.model.accountvisitors;

//todo make this into a generic class
public abstract class ReturningAccountVisitor implements IAccountVisitor {
    protected Object val;

    public Object getRecentVal() {
        return val;
    }
}
