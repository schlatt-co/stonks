package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class HideSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    new CompanySelectorGui.Builder()
        .title("Select company to hide")
        .companies(Repo.getInstance().companies().getAllWhere(c -> !c.hidden))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Hide " + company.name + "?")
            .yes(() ->
                Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, company.verified, true)
            )
            .show(player))
        .show(player);
  }
}
