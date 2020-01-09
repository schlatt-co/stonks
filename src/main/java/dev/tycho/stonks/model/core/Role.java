package dev.tycho.stonks.model.core;

public enum Role {

  CEO(3), Manager(2), Employee(1), Intern(0);

  private final int power;

  Role(int power) {
    this.power = power;
  }

  public boolean hasPermission(Role role) {
    return power >= role.getPower();
  }

  public int getPower() {
    return power;
  }
}
