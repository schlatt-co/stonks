package dev.tycho.stonks.command.base.validators;

import java.util.HashMap;

public class ArgumentStore {

  private final HashMap<String, ArgumentValue<?>> arguments = new HashMap<>();

  public void addArgument(String name, ArgumentValue<?> value) {
    arguments.put(name, value);
  }

  public ArgumentValue<?> getArgument(String name) {
    if (!arguments.containsKey(name)) {
      throw new IllegalArgumentException("Argument with name " + name + " not found");
    }
    return arguments.get(name);
  }
}
