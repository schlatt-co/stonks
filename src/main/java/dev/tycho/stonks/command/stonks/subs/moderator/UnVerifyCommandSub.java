package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.SimpleCommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class UnVerifyCommandSub extends SimpleCommandSub {

  @Override
  public void execute(Player player) {
    new CompanySelectorGui.Builder()
        .title("Select company to unverify")
        .companies(Repo.getInstance().companies().getAllWhere(c -> c.verified))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Unverify " + company.name + "?")
            .yes(() -> Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, false, company.hidden))
            .show(player))
        .show(player);
  }
}
