package dev.tycho.stonks.command.stonks.subs.account;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentStore;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.AccountListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class AccountsCommandSub extends ModularCommandSub {

  public AccountsCommandSub() {
    super(new CompanyValidator("company"));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player, ArgumentStore store) {
    Company company = getArgument("company", store);
    new AccountListGui(company).show(player);
  }
}
