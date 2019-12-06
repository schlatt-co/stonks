package dev.tycho.stonks.command.base.validators;

import java.util.regex.Pattern;

public class CurrencyValidator extends ArgumentValidator<Double> {
  public CurrencyValidator(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    if (!Pattern.matches("([0-9]*)\\.?([0-9]*)?", str)) {
      return false;
    }
    this.value = Double.parseDouble(str);
    return true;
  }

  @Override
  public String getPrompt() {
    return getName() + " must be a positive number";
  }
}
