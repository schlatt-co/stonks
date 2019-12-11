package dev.tycho.stonks.command.subs.company;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.CompanyInfoGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class InfoCommandSub extends ModularCommandSub {

  public InfoCommandSub() {
    super(new CompanyValidator("company"));
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    new CompanyInfoGui(company).show(player);
  }
}
