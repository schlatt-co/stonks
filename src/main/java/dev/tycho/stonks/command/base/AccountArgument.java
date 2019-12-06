package dev.tycho.stonks.command.base;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import org.apache.commons.lang.StringUtils;

public class AccountArgument extends Argument<Account> {

  public AccountArgument(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    if (!StringUtils.isNumeric(str)) {
      return false;
    }
    value = Repo.getInstance().accountWithId(Integer.parseInt(str));
    if (value == null) return false;
    return true;
  }

  @Override
  public String getPrompt() {
    return "must be a valid account id";
  }
}
