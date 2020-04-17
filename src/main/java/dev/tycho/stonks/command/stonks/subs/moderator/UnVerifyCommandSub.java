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

public class UnVerifyCommandSub extends ModularCommandSub {
  public UnVerifyCommandSub() {
    super(ArgumentValidator.optionalAndConcatIfLast(new CompanyValidator("company")));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Company comp = getArgument("company");
    if (comp != null) {
      unverify(comp);
      return;
    }

    new CompanySelectorGui.Builder()
        .title("Select company to unverify")
        .companies(Repo.getInstance().companies().getAllWhere(c -> c.verified))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Unverify " + company.name + "?")
            .yes(() -> unverify(company))
            .show(player))
        .show(player);
  }
  private void unverify(Company company) {
    Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, false, company.hidden);

    Repo.getInstance().sendMessageToAllOnlineManagers(
        company, ChatColor.GOLD + company.name + ChatColor.RED +" was just unverified!");
  }
}
