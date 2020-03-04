package dev.tycho.stonks.command.stonks.subs.account;

import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.AccountListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class AccountsSubCommand extends ModularSubCommand {

  public AccountsSubCommand() {
    super(new CompanyValidator("company"));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    new AccountListGui(company).show(player);
  }
}
