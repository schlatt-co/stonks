package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class UnHideCommandSub extends ModularCommandSub {

  public UnHideCommandSub() {
    super(ArgumentValidator.optionalAndConcatIfLast(new CompanyValidator("company")));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }
  @Override
  public void execute(Player player) {
    Company comp = getArgument("company");
    if (comp != null) {
      unHide(comp);
      return;
    }

    new CompanySelectorGui.Builder()
        .title("Select company to unhide")
        .companies(Repo.getInstance().companies().getAllWhere(c -> c.hidden))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Unhide " + company.name + "?")
            .yes(() -> unHide(company))
            .show(player))
        .show(player);
  }

  private void unHide(Company company) {
    Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, company.verified, false);
  }
}
