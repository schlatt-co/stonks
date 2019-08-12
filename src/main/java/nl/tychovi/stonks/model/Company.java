package nl.tychovi.stonks.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Company {

  private List<Account> accounts = new ArrayList<>();
  private String name;
  private UUID uuid;
  private boolean dirty = false;

  public Company(UUID uuid, String name) {
    this.uuid = uuid;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public boolean setName(String name) {
    //Don't allow blank names
    if (name == null) {
      return false;
    }
    this.name = name;
    return true;
  }

  public void addAccount(Account a) {
    accounts.add(a);
  }

  public boolean removeAccount(Account a) {
    if (accounts.contains(a)) {
      accounts.remove(a);
      return true;
    } else {
      return false;
    }

  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public UUID id() {
    return uuid;
  }

  public void dirty() {
    dirty = true;
  }

  public void clean() {
    dirty = false;
  }
}
