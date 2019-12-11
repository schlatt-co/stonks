package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collection;

public class MessageManager extends SpigotModule {

  public MessageManager(Stonks plugin) {
    super("Message Manager", plugin);
  }

  public static void sendHelpMessage(Player player, String label) {
    player.sendMessage(ChatColor.AQUA + "--------------------");
    player.sendMessage(ChatColor.GOLD + "To view all commands and more info about the plugin please go to:");
    player.sendMessage(ChatColor.GOLD + "https://stonks.tycho.dev/");
    player.sendMessage(ChatColor.AQUA + "--------------------");
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Collection<Member> invites = Repo.getInstance().getInvites(event.getPlayer());
    Collection<Subscription> subscriptions = Repo.getInstance().subscriptions().getAllWhere(s -> s.playerUUID.equals(event.getPlayer().getUniqueId()));

    if (invites.size() > 0) {
      event.getPlayer().sendMessage(ChatColor.AQUA + "You have " + ChatColor.GREEN + invites.size() + ChatColor.AQUA + " open company invites! Do " + ChatColor.GREEN + "/stonks invites" + ChatColor.AQUA + " to view them.");
      event.getPlayer().sendMessage("============");
    }
    int numOverdue = 0;
    for (Subscription subscription : subscriptions) {
      Service service = Repo.getInstance().services().get(subscription.servicePk);
      if (Subscription.isOverdue(service, subscription)) numOverdue++;
    }

    if (numOverdue > 0) {
      event.getPlayer().sendMessage(ChatColor.AQUA + "You have " + ChatColor.RED + numOverdue + ChatColor.AQUA + " overdue subscriptions. Type " + ChatColor.GREEN + "/stonks subscriptions" + ChatColor.AQUA + " to view them.");
      event.getPlayer().sendMessage("============");
    }


  }
}
