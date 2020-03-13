package dev.tycho.stonks.command.stonks.subs.company;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentStore;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.CompanyInfoGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class InfoCommandSub extends ModularCommandSub {

  public InfoCommandSub() {
    super(new CompanyValidator("company"));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player, ArgumentStore store) {
    Company company = getArgument("company", store);
    new CompanyInfoGui(company).show(player);
  }
}
