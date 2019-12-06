package dev.tycho.stonks.command.base;

import java.util.regex.Pattern;

public class CurrencyArgument extends Argument<Double> {
  public CurrencyArgument(String name) {
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
