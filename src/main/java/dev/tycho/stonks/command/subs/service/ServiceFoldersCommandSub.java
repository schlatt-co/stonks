package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.ServiceFoldersListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class ServiceFoldersCommandSub extends ModularCommandSub {
  public ServiceFoldersCommandSub() {
    super(new CompanyValidator("company"));
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    Stonks.newChain()
        .asyncFirst(() -> new ServiceFoldersListGui(company))
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
