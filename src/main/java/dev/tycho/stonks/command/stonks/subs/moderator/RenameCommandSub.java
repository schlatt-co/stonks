package dev.tycho.stonks.command.stonks.subs.moderator;

import dev.tycho.stonks.api.event.CompanyRenameEvent;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RenameCommandSub extends ModularCommandSub {

  public RenameCommandSub() {
    super(ArgumentValidator.concatIfLast(new StringValidator("new_name")));
    setPermissions("stonks.mod.rename", "stonks.mod");
  }

  @Override
  public void execute(Player player) {
    String newName = getArgument("new_name");
    new CompanySelectorGui.Builder()
        .title("Select company to rename")
        .companies(Repo.getInstance().companies().getAll())
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Rename " + company.name + " to" + newName + "?")
            .yes(() -> {
              Bukkit.getPluginManager().callEvent(new CompanyRenameEvent(company, newName));
              Repo.getInstance().modifyCompany(company.pk, newName, company.logoMaterial, company.verified, company.hidden);
            })
            .show(player))
        .show(player);
  }
}
