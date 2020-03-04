package dev.tycho.stonks.command.stonks.subs.service;

import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.autocompleters.CurrencyAutocompleter;
import dev.tycho.stonks.command.base.autocompleters.OptionListAutocompleter;
import dev.tycho.stonks.command.base.validators.CurrencyValidator;
import dev.tycho.stonks.command.base.validators.DoubleValidator;
import dev.tycho.stonks.command.base.validators.IntegerValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.service.Service;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class CreateServiceSubCommand extends ModularSubCommand {

  public CreateServiceSubCommand() {
    super(new DoubleValidator("duration"), new CurrencyValidator("cost"), new IntegerValidator("max_subs"), new StringValidator("name", 40));
    addAutocompleter("duration", new OptionListAutocompleter("1", "2", "7", "[days]"));
    addAutocompleter("cost", new CurrencyAutocompleter());
    addAutocompleter("max_subs", new OptionListAutocompleter("0 [unlimited]", "1", "2", "10"));
  }
  @Override
  public void execute(Player player) {
    double duration = getArgument("duration");
    double cost = getArgument("cost");
    int maxSubs = getArgument("max_subs");
    String name = getArgument("name");

    new CompanySelectorGui.Builder()
        .companies(Repo.getInstance().companiesWhereManager(player))
        .title("Select a company")
        .companySelected(company -> new AccountSelectorGui.Builder()
            .title("Select the account profits go to")
            .company(company)
            .accountSelected(
                account -> createService(player, duration, cost, maxSubs, name, company, account)
            ).show(player))
        .show(player);
  }

  void createService(Player player, double duration, double cost, int maxSubs, String name, Company company, Account account) {
    //Check for the same name
    for (Account a : company.accounts) {
      for (Service service : a.services) {
        if (service.name.equals(name)) {
          sendMessage(player, "A service with the same name already exists for this company");
          return;
        }
      }
    }

    if (duration <= 0.5) {
      sendMessage(player, "Service duration must be greater than 0.5 (12 hours)");
      return;
    }

    if (cost < 0) {
      sendMessage(player, "A service cannot have a negative cost. Nice try");
      return;
    }

    if (!name.matches("[0-9a-zA-Z&+_]{2,40}")) {
      sendMessage(player, "Invalid name. Please try again. You may have used special characters or it is too long");
      return;
    }

    if (StringUtils.isNumeric(name)) {
      sendMessage(player, "A company name cannot be a number!");
      return;
    }

    //Only verified companies can create services
    if (!company.verified) {
      sendMessage(player, "Your company must be verified before you can create a service. Ask a moderator to consider verifying your company.");
      return;
    }

    Repo.getInstance().createService(name, duration, cost, maxSubs, account);
    sendMessage(player, "Service created!");
  }
}
