package dev.tycho.stonks.model.accountvisitors;


public abstract class ReturningAccountVisitor<T> implements IAccountVisitor {
  protected T val;

  public T getRecentVal() {
    return val;
  }
}
