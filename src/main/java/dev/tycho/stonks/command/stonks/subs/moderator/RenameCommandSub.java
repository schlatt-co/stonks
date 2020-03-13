package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.api.event.CompanyRenameEvent;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ArgumentStore;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RenameCommandSub extends ModularCommandSub {

  public RenameCommandSub() {
    super(new StringValidator("new_name").setConcat());
  }

  @Override
  public void execute(Player player, ArgumentStore store) {
    String newName = getArgument("new_name", store);
    new CompanySelectorGui.Builder()
        .title("Select company to rename")
        .companies(Repo.getInstance().companies().getAll())
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Rename " + company.name + " to" + newName + "?")
            .yes(() -> {
              Bukkit.getPluginManager().callEvent(new CompanyRenameEvent(company, newName));
              Repo.getInstance().modifyCompany(company, newName, company.logoMaterial, company.verified, company.hidden);
            })
            .show(player))
        .show(player);
  }
}