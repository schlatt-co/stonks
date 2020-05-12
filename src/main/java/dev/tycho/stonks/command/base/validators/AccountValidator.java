package dev.tycho.stonks.command.base.validators;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import org.apache.commons.lang.StringUtils;

public class AccountValidator extends ArgumentValidator<Account> {

  public AccountValidator(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    if (!StringUtils.isNumeric(str)) {
      return false;
    }
    value = Repo.getInstance().accountWithPk(Integer.parseInt(str));
    return value != null;
  }

  @Override
  public String getPrompt() {
    return "must be a valid account id";
  }
}
