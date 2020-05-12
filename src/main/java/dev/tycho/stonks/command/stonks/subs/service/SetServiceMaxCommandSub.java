package dev.tycho.stonks.command.stonks.subs.service;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.OptionListAutocompleter;
import dev.tycho.stonks.command.base.validators.IntegerValidator;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ServiceSelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.service.Service;
import org.bukkit.entity.Player;

public class SetServiceMaxCommandSub extends ModularCommandSub {

  public SetServiceMaxCommandSub() {
    super(new IntegerValidator("max_subs"));
    addAutocompleter("max_subs", new OptionListAutocompleter("0 [unlimited]", "1", "2", "10"));
  }

  @Override
  public void execute(Player player) {

    new CompanySelectorGui.Builder()
        .companies(Repo.getInstance().companiesWhereManager(player))
        .title("Select a company")
        .companySelected(company -> new ServiceSelectorGui.Builder()
            .company(company)
            .title("Select a service")
            .serviceSelected(
                service -> changeServiceMaxSubs(player, getArgument("max_subs"), company, service)
            ).show(player))
        .show(player);
  }

  private void changeServiceMaxSubs(Player player, int maxSubs, Company company, Service service) {
    Member member = company.getMember(player);

    if (member == null || !member.hasManagamentPermission()) {
      sendMessage(player, "You don't have management perms for this company");
      return;
    }

    if (maxSubs > 0 && maxSubs < service.subscriptions.size()) {
      sendMessage(player, "You can't set the max subscriptions lower than the current number of subscriptions");
      return;
    }

    Repo.getInstance().modifyService(service.pk, service.name, service.duration, service.cost, maxSubs);
    sendMessage(player, "Max subs updated to " + maxSubs);
  }
}
