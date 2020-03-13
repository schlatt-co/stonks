package dev.tycho.stonks.command.base.validators;

import org.apache.commons.lang.StringUtils;

public class IntegerValidator extends ArgumentValidator<Integer> {
  public IntegerValidator(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    if (!StringUtils.isNumeric(str)) return false;
    value = Integer.parseInt(str);
    return true;
  }

  @Override
  public String getPrompt() {
    return "must be a whole number (integer)";
  }
}
