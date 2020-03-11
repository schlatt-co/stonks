package dev.tycho.stonks.command.base.validators;

import org.apache.commons.lang.StringUtils;

public class IntegerValidator extends ArgumentProvider<Integer> {

  public IntegerValidator(String name) {
    super(name, Integer.class);
  }

  @Override
  public Integer provideArgument(String arg) {
    if (!StringUtils.isNumeric(arg)) {
      return null;
    }
    return Integer.parseInt(arg);
  }

  @Override
  public String getHelp() {
    return "Must be a whole number (Integer).";
  }
}
