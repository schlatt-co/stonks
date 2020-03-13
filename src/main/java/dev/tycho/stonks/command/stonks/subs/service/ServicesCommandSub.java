package dev.tycho.stonks.command.stonks.subs.service;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.command.base.validators.ArgumentStore;
import dev.tycho.stonks.gui.ServicesListGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import org.bukkit.entity.Player;

public class ServicesCommandSub extends ModularCommandSub {

  public ServicesCommandSub() {
    super(new AccountValidator("account"));
  }

  @Override
  public void execute(Player player, ArgumentStore store) {
    Account account = getArgument("account", store);
    new ServicesListGui(Repo.getInstance().companies().get(account.companyPk), account).show(player);
  }
}
