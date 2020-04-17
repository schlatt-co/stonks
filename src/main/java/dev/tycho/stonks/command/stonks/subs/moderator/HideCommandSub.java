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

public class HideCommandSub extends ModularCommandSub {

  public HideCommandSub() {
    super(ArgumentValidator.optionalAndConcatIfLast(new CompanyValidator("company")));
    addAutocompleter("company", new CompanyNameAutocompleter());
    setPermissions("stonks.mod.hide", "stonks.mod");
  }

  @Override
  public void execute(Player player) {
    Company comp = getArgument("company");
    if (comp != null) {
      hide(comp);
      return;
    }


    new CompanySelectorGui.Builder()
        .title("Select company to hide")
        .companies(Repo.getInstance().companies().getAllWhere(c -> !c.hidden))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Hide " + company.name + "?")
            .yes(() -> hide(company))
            .show(player))
        .show(player);
  }

  private void hide(Company company) {
    Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, company.verified, true);
  }
}
