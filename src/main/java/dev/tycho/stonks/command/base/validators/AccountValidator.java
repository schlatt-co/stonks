package dev.tycho.stonks.command.base.validators;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import org.apache.commons.lang.StringUtils;

public class AccountValidator extends ArgumentProvider<Account> {

  public AccountValidator(String name) {
    super(name, Account.class);
  }

  @Override
  public Account provideArgument(String arg) {
    if (!StringUtils.isNumeric(arg)) {
      return null;
    }
    return Repo.getInstance().accountWithId(Integer.parseInt(arg));
  }

  @Override
  public String getHelp() {
    return "Must be a valid account pk.";
  }
}
