package dev.tycho.stonks.command.base.validators;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.apache.commons.lang.StringUtils;

public class CompanyValidator extends ArgumentValidator<Company> {

  public CompanyValidator(String name) {
    super(name);
    this.concatIfLastArg = true;
  }

  @Override
  public boolean provide(String str) {
    //If the name is a number then it is actually the PK (e.g. in some gui screens)
    if (StringUtils.isNumeric(str)) {
      int pk = Integer.parseInt(str);
      this.value = Repo.getInstance().companies().get(pk);
    } else {
      this.value = Repo.getInstance().companyWithName(str);
    }
    return this.value != null;
  }

  @Override
  public String getPrompt() {
    return "Company not found";
  }
}
