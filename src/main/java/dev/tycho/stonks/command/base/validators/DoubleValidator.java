package dev.tycho.stonks.command.base.validators;

import org.apache.commons.lang.StringUtils;

public class DoubleValidator extends ArgumentValidator<Double> {
  public DoubleValidator(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    if (!StringUtils.isNumeric(str)) return false;
    value = Double.parseDouble(str);
    return true;
  }

  @Override
  public String getPrompt() {
    return "must be a number (decimal points allowed)";
  }

}
