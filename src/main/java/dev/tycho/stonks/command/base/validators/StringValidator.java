package dev.tycho.stonks.command.base.validators;

public class StringValidator extends ArgumentProvider<String> {
  private final int maxLength;

  public StringValidator(String name) {
    this(name, 255);
  }

  public StringValidator(String name, int maxLength) {
    super(name, String.class);
    this.maxLength = maxLength;
  }

  @Override
  public String provideArgument(String arg) {
    if (arg.length() > maxLength) {
      return null;
    }
    return arg;
  }

  @Override
  public String getHelp() {
    return "Must be a string (Max length of " + maxLength + ".";
  }
}
