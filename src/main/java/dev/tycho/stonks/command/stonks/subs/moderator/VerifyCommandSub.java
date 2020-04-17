package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VerifyCommandSub extends ModularCommandSub {

  public VerifyCommandSub() {
    super(ArgumentValidator.optionalAndConcatIfLast(new CompanyValidator("company")));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Company comp = getArgument("company");
    if (comp != null) {
      Verify(comp);
      return;
    }
    new CompanySelectorGui.Builder()
        .title("Select company to verify")
        .companies(Repo.getInstance().companies().getAllWhere(c -> !c.verified))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Verify " + company.name + "?")
            .yes(() -> Verify(company))
            .show(player))
        .show(player);
  }

  private void Verify(Company company) {
    Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, true, company.hidden);

    Repo.getInstance().sendMessageToAllOnlineManagers(
        company, ChatColor.GOLD + company.name + ChatColor.GREEN +" was just verified!");
  }

}

