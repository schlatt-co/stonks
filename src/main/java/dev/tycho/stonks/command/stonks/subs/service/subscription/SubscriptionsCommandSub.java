package dev.tycho.stonks.command.stonks.subs.service.subscription;

import dev.tycho.stonks.command.base.SimpleCommandSub;
import dev.tycho.stonks.gui.PlayerSubscriptionListGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.entity.Player;

public class SubscriptionsCommandSub extends SimpleCommandSub {

  @Override
  public void execute(Player player) {
    new PlayerSubscriptionListGui(
        Repo.getInstance().subscriptions().getAllWhere(s -> s.playerUUID.equals(player.getUniqueId())))
        .show(player);
  }
}
