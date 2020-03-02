package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.ServiceFoldersListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class ServiceFoldersSubCommand extends ModularSubCommand {
  public ServiceFoldersSubCommand() {
    super(new CompanyValidator("company"));
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    new ServiceFoldersListGui(company).show(player);
  }
}
