package dev.tycho.stonks.command.base;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;

public class CompanyArgument extends Argument<Company> {

  public CompanyArgument(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    this.value = Repo.getInstance().companyWithName(str);
    return str == null;
  }

  @Override
  public String getPrompt() {
    return "Company not found";
  }
}
