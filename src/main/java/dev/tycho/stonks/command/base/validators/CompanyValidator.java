package dev.tycho.stonks.command.base.validators;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.apache.commons.lang.StringUtils;

public class CompanyValidator extends ArgumentProvider<Company> {

  public CompanyValidator(String name) {
    super(name, Company.class, false, true);
  }

  @Override
  public Company provideArgument(String arg) {
    if (StringUtils.isNumeric(arg)) {
      int pk = Integer.parseInt(arg);
      return Repo.getInstance().companies().get(pk);
    } else {
      return Repo.getInstance().companyWithName(arg);
    }
  }

  @Override
  public String getHelp() {
    return "Must be a company name or pk.";
  }
}
