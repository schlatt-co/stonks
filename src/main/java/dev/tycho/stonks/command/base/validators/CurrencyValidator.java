package dev.tycho.stonks.command.base.validators;

import java.util.regex.Pattern;

public class CurrencyValidator extends ArgumentProvider<Double> {

  public CurrencyValidator(String name) {
    super(name, Double.class);
  }

  @Override
  public Double provideArgument(String arg) {
    if (!Pattern.matches("([0-9]*)\\.?([0-9]*)?", arg)) {
      return null;
    }
    return Double.parseDouble(arg);
  }

  @Override
  public String getHelp() {
    return "Must be a positive number.";
  }
}
