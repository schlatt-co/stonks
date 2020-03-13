package dev.tycho.stonks.command.base.validators;

public class ArgumentValue<T> {

  private final T value;

  public ArgumentValue(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }
}
