package dev.tycho.stonks.command.subs.moderator;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class VerifySubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    new CompanySelectorGui.Builder()
        .title("Select company to verify")
        .companies(Repo.getInstance().companies().getAllWhere(c -> !c.verified))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Verify " + company.name + "?")
            .yes(() -> Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, true, company.hidden))
            .show(player))
        .show(player);
  }
}
