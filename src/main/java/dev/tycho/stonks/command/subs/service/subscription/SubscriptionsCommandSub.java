package dev.tycho.stonks.command.subs.service.subscription;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.PlayerSubscriptionListGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubscriptionsCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    new PlayerSubscriptionListGui(
        Repo.getInstance().subscriptions().getAllWhere(s->s.playerUUID.equals(player.getUniqueId())))
        .show(player);
  }
}
