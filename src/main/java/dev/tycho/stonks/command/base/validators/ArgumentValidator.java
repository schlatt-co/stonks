package dev.tycho.stonks.command.base.validators;

public abstract class ArgumentValidator<T> {
  private final String name;
  protected boolean concatIfLastArg;
  protected boolean optional;
  protected T value;

  public ArgumentValidator(String name) {
    this.name = name;
    this.concatIfLastArg = false;
    this.optional = false;
  }

  public static <Z> ArgumentValidator<Z> optional(ArgumentValidator<Z> arg) {
    arg.setOptional();
    return arg;
  }

  public static <Z> ArgumentValidator<Z> concatIfLast(ArgumentValidator<Z> arg) {
    arg.setConcat();
    return arg;
  }

  public static <Z> ArgumentValidator<Z> optionalAndConcatIfLast(ArgumentValidator<Z> arg) {
    arg.setConcat();
    arg.setOptional();
    return arg;
  }

  public T get() {
    T ret = value;
    value = null;
    return ret;
  }

  public abstract boolean provide(String str);

  public final String getName() {
    return name;
  }

  private void setConcat() {
    this.concatIfLastArg = true;
  }

  public final boolean concatIfLastArg() {
    return concatIfLastArg;
  }

  public final boolean isOptional() {
    return optional;
  }

  private void setOptional() {
    this.optional = true;
  }

  public String getUsage() {
    return "<" + name + ">";
  }

  public abstract String getPrompt();
}