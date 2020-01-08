package dev.tycho.stonks.api.perks;

import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;

public abstract class CompanyPerk {

  private final String name;
  private final int price;
  private final String[] description;

  public CompanyPerk(String name, int price, String... description) {
    this.name = name;
    this.price = price;
    this.description = description;
  }

  public abstract void onPurchase(Company company, Member purchaser);

  public String getName() {
    return name;
  }

  public int getPrice() {
    return price;
  }

  public String[] getDescription() {
    return description;
  }
}
