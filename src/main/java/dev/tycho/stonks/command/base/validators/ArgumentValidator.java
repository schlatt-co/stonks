package dev.tycho.stonks.command.base.validators;

@SuppressWarnings("rawtypes")
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

  public static ArgumentValidator optional(ArgumentValidator arg) {
    arg.setOptional(true);
    return arg;
  }

  public static ArgumentValidator concatIfLast(ArgumentValidator arg) {
    arg.setConcat(true);
    return arg;
  }

  public static ArgumentValidator optionalAndConcatIfLast(ArgumentValidator arg) {
    arg.setConcat(true);
    arg.setOptional(true);
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

  private void setConcat(boolean val) {
    this.concatIfLastArg = val;
  }

  public final boolean concatIfLastArg() {
    return concatIfLastArg;
  }

  public final boolean isOptional() {
    return optional;
  }

  private void setOptional(boolean val) {
    this.optional = val;
  }

  public String getUsage() {
    return "<" + name + ">";
  }

  public abstract String getPrompt();
}