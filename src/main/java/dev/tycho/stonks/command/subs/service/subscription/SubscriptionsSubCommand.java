package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.gui.PlayerSubscriptionListGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class SubscriptionsSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    new PlayerSubscriptionListGui(
        Repo.getInstance().subscriptions().getAllWhere(s -> s.playerUUID.equals(player.getUniqueId())))
        .show(player);
  }
}
