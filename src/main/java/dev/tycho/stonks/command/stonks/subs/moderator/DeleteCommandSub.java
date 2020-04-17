package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeleteCommandSub extends ModularCommandSub {

  public DeleteCommandSub() {
    super(ArgumentValidator.optionalAndConcatIfLast(new CompanyValidator("company")));
    addAutocompleter("company", new CompanyNameAutocompleter());
    setPermissions("stonks.mod.delete", "stonks.mod");
  }

  @Override
  public void execute(Player player) {
    Company comp = getArgument("company");
    if (comp != null) {
      delete(comp, player);
      return;
    }


    new CompanySelectorGui.Builder()
        .title("Select company to delete")
        .companies(Repo.getInstance().companies().getAllWhere(c -> !c.hidden))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("ARE YOU SURE " + company.name + "?")
            .yes(() -> delete(company, player))
            .show(player))
        .show(player);
  }

  private void delete(Company company, Player player) {
    Repo.getInstance().modifyCompany(company, "_", Material.BARRIER.name(), false, true);
    sendMessage(player, "Company Deleted");
  }
}