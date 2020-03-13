package dev.tycho.stonks.command.stonks.subs.service;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.ServiceFoldersListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class ServiceFoldersCommandSub extends ModularCommandSub {
  public ServiceFoldersCommandSub() {
    super(new CompanyValidator("company"));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    new ServiceFoldersListGui(company).show(player);
  }
}
