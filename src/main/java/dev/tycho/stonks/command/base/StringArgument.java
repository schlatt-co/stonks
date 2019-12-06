package dev.tycho.stonks.command.base;

public class StringArgument extends Argument<String> {
  private final int maxLength;

  public StringArgument(String name) {
    super(name);
    this.maxLength = 255;
  }

  public StringArgument(String name, int maxLength) {
    super(name);
    this.maxLength = maxLength;
  }

  @Override
  public boolean provide(String str) {
    this.value = str;
    if (str.length() > maxLength) {
      return false;
    }
    return true;
  }

  @Override
  public String getPrompt() {
    if (value.length() > maxLength) {
      return getName() + "Must be fewer than " + maxLength + " letters long";
    } else {
      return "Expected a string";
    }
  }
}
