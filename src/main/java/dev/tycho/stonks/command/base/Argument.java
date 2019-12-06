package dev.tycho.stonks.command.base;

public abstract class Argument<T> {
  private final String name;
  private boolean concatIfLastArg;
  private boolean optional;
  protected T value;

  public Argument(String name) {
    this.name = name;
    this.concatIfLastArg = false;
    this.optional = false;
  }

  public T get() {
    return value;
  }

  public abstract boolean provide(String str);

  public final String getName() {
    return name;
  }

  private final void setConcat(boolean val) {
    this.concatIfLastArg = val;
  }
  private final void setOptional(boolean val) {
    this.optional = val;
  }

  public final boolean concatIfLastArg() {
    return concatIfLastArg;
  }

  public final boolean isOptional() {
    return optional;
  }

  public String getUsage() {
    return "<" + name + ">";
  }

  public abstract String getPrompt();


  public static Argument optional(Argument arg) {
    arg.setOptional(true);
    return  arg ;
  }
}
