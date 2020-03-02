package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.validators.ServiceValidator;
import dev.tycho.stonks.gui.SubscriberListGui;
import org.bukkit.entity.Player;

public class SubscribersSubCommand extends ModularSubCommand {

  public SubscribersSubCommand() {
    super(new ServiceValidator("service"));
  }

  @Override
  public void execute(Player player) {
    new SubscriberListGui(getArgument("service")).show(player);
  }
}
