package dev.tycho.stonks.api.event.transaction;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;

public class AccountTransactionUser implements ITransactionUser {
  public final Account account;
  public final String displayName;

  public AccountTransactionUser(Account account) {
    this.account = account;
    this.displayName = Repo.getInstance().companies().get(account.companyPk).name + " #" + account.pk;
  }

  @Override
  public String userDisplayName() {
    return displayName;
  }
}
