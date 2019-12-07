package dev.tycho.stonks.command.base.validators;

public class StringValidator extends ArgumentValidator<String> {
  private final int maxLength;

  public StringValidator(String name) {
    super(name);
    this.maxLength = 255;
  }

  public StringValidator(String name, int maxLength) {
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
      return "Must be fewer than " + maxLength + " letters long";
    } else {
      return "Expected a string";
    }
  }
}
