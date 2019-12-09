package dev.tycho.stonks.scheduledtasks;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubscriptionCheckTask implements Runnable {

  public SubscriptionCheckTask() {
    System.out.println("New subscription payment task scheduled");
  }

  @Override
  public void run() {
    System.out.println("Automatically renewing subscriptions");
    //We want to go through all subscriptions

    List<Subscription> subscriptions = Repo.getInstance().subscriptions().getAll();
    for (Subscription subscription : subscriptions) {
      renewSubscription(subscription);
      cancelSubscriptionIfOverdue(subscription);
    }
  }

  private void renewSubscription(Subscription subscription) {
    Service service = Repo.getInstance().services().get(subscription.servicePk);

    OfflinePlayer player = Bukkit.getOfflinePlayer(subscription.playerUUID);
    Player onlinePlayer = null;
    if (player.isOnline()) onlinePlayer = player.getPlayer();

    if (Subscription.isOverdue(service, subscription)) {
      System.out.println("Auto billing " + player.getName() + " $" + service.cost + " for service id " + service.pk);
      //Subscription is overdue and we must renew it
      if (onlinePlayer != null)
        sendMessage(onlinePlayer, ChatColor.YELLOW + "Your subscription for " + service.name + " has ended." + ChatColor.GREEN + " Automatically renewing it now.....");

      //Try and charge them the amount due
      if (!Stonks.economy.withdrawPlayer(player, service.cost).transactionSuccess()) {
        //Player does not have enough money
        System.out.println("(failed - they didn't have enough money)");
        if (onlinePlayer != null)
          sendMessage(onlinePlayer, ChatColor.RED + "Failed to renew. You don't have enough money, please get more then manually resubscribe");
      } else {
        //Payment success
        //Update that the subscription is paid
        Repo.getInstance().paySubscription(player.getUniqueId(), subscription, service);

        //Notify user
        if (onlinePlayer != null) {
          sendMessage(onlinePlayer, ChatColor.GREEN +
              "Success! Your subscription will be auto-renewed again in " + ChatColor.YELLOW + service.duration
              + ChatColor.GREEN + " days time. You can cancel at any time by doing /stonks subscriptions");
        } else {
          Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mail send " +
              player.getName() + " you have been billed $" + service.cost + " for the service " + service.name);
        }
        System.out.println("(success)");
      }
    }
  }

  private void cancelSubscriptionIfOverdue(Subscription subscription) {
    Service service = Repo.getInstance().services().get(subscription.servicePk);
    OfflinePlayer player = Bukkit.getOfflinePlayer(subscription.playerUUID);
    //If it is still overdue (they didnt pay it or failed to pay it)
    if (Subscription.isOverdue(service, subscription)) {
      if (Subscription.getDaysOverdue(service, subscription) > service.duration) {
        //They have not paid their subscription in over the duration
        //Auto cancel it
        Repo.getInstance().deleteSubscription(subscription, service);
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mail send " +
            player.getName() + " Your subscription to " + service.name +
            " has been cancelled since you did not have enough money to automatically pay it.");
      }
    }
  }

  private void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }
}