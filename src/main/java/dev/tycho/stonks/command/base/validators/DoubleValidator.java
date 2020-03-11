package dev.tycho.stonks.command.base.validators;

import org.apache.commons.lang.math.NumberUtils;

public class DoubleValidator extends ArgumentProvider<Double> {

  public DoubleValidator(String name) {
    super(name, Double.class);
  }

  @Override
  public Double provideArgument(String str) {
    if (!NumberUtils.isNumber(str)) {
      return null;
    }
    return Double.parseDouble(str);
  }

  @Override
  public String getHelp() {
    return "Must be a positive number.";
  }

}
