package dev.tycho.stonks.command.subs.company;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.PerkListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

public class PerksCommandSub extends ModularCommandSub {
  public PerksCommandSub() {
    super(new CompanyValidator("company"));
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    new PerkListGui(company).show(player);
  }
}
