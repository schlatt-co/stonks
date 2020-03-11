package dev.tycho.stonks.command.base.validators;

public abstract class ArgumentProvider<T> {

  private final String name;
  private final Class<T> provider;
  private boolean optional;
  private boolean concat;

  public ArgumentProvider(String name, Class<T> provider) {
    this(name, provider, false);
  }

  public ArgumentProvider(String name, Class<T> provider, boolean optional) {
    this(name, provider, optional, false);
  }

  public ArgumentProvider(String name, Class<T> provider, boolean optional, boolean concat) {
    this.name = name;
    this.provider = provider;
    this.optional = optional;
    this.concat = concat;
  }

  public ArgumentProvider<T> setOptional() {
    optional = true;
    return this;
  }

  public ArgumentProvider<T> setConcat() {
    this.concat = true;
    return this;
  }

  public ArgumentProvider<T> setOptionalAndConcat() {
    optional = true;
    concat = true;
    return this;
  }

  public T parseArgument(String arg) {
    if (arg == null) {
      return null;
    }
    return provideArgument(arg);
  }

  public abstract T provideArgument(String arg);

  public abstract String getHelp();

  public String getName() {
    return name;
  }

  public Class<T> getProvider() {
    return provider;
  }

  public boolean isOptional() {
    return optional;
  }

  public boolean isConcat() {
    return concat;
  }
}
