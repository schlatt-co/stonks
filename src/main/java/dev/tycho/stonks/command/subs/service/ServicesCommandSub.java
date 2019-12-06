package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.gui.ServicesListGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import org.bukkit.entity.Player;

public class ServicesCommandSub extends ModularCommandSub {

  public ServicesCommandSub() {
    super(new AccountValidator("account"));
  }

  @Override
  public void execute(Player player) {
    Account account = getArgument("account");

    Stonks.newChain()
        .asyncFirst(() -> new ServicesListGui(Repo.getInstance().companies().get(account.companyPk), account))
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
