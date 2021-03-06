package dev.tycho.stonks.command.stonks.subs.service;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ServiceValidator;
import dev.tycho.stonks.gui.ServiceInfoGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.service.Service;
import org.bukkit.entity.Player;

public class ServiceInfoCommandSub extends ModularCommandSub {

  public ServiceInfoCommandSub() {
    super(new ServiceValidator("service"));
  }

  @Override
  public void execute(Player player) {
    Service service = getArgument("service");
    if (service == null) {
      player.sendMessage("Service id not found");
      return;
    }
    new ServiceInfoGui(service, Repo.getInstance().accountWithPk(service.accountPk)).show(player);
  }
}
