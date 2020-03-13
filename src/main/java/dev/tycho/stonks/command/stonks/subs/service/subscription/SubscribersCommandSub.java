package dev.tycho.stonks.command.stonks.subs.service.subscription;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ServiceValidator;
import dev.tycho.stonks.gui.SubscriberListGui;
import org.bukkit.entity.Player;

public class SubscribersCommandSub extends ModularCommandSub {

  public SubscribersCommandSub() {
    super(new ServiceValidator("service"));
  }

  @Override
  public void execute(Player player) {
    new SubscriberListGui(getArgument("service")).show(player);
  }
}
