package dev.tycho.stonks.command.base.validators;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;

public class CompanyValidator extends ArgumentValidator<Company> {

  public CompanyValidator(String name) {
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
