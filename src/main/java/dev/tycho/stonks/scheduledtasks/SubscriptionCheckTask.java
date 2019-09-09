package dev.tycho.stonks.scheduledtasks;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class SubscriptionCheckTask implements Runnable {

  public SubscriptionCheckTask() {
    System.out.println("New subscription payment task scheduled");
  }

  @Override
  public void run() {
    System.out.println("Automatically renewing subscriptions");
    //We want to go through all subscriptions
    List<Subscription> subscriptions;
    try {
      subscriptions = DatabaseHelper.getInstance().getDatabaseManager().getSubscriptionDao().queryForAll();
    } catch (SQLException e) {
      e.printStackTrace();
      Bukkit.broadcastMessage("Error updating subscriptions");
      return;
    }

    for (Subscription subscription : subscriptions) {
      renewSubscription(subscription);
      cancelSubscriptionIfOverdue(subscription);
    }
  }

  private void renewSubscription(Subscription subscription) {
    Service service = subscription.getService();
    OfflinePlayer player = Bukkit.getOfflinePlayer(subscription.getPlayerId());
    Player onlinePlayer = null;
    if (player.isOnline()) onlinePlayer = player.getPlayer();

    if (subscription.isOverdue() && subscription.isAutoPay()) {
      System.out.println("Auto billing " + player.getName() + " $" + service.getCost() + " for service id " + service.getId());
      //Subscription is overdue and we must renew it
      if (onlinePlayer != null)
        sendMessage(onlinePlayer, ChatColor.YELLOW + "Your subscription for " + service.getName() + " has ended." + ChatColor.GREEN + " Automatically renewing it now.....");

      //Try and charge them the amount due
      if (!Stonks.economy.withdrawPlayer(player, service.getCost()).transactionSuccess()) {
        //Player does not have enough money
        System.out.println("(failed - they didn't have enough money)");
        if (onlinePlayer != null)
          sendMessage(onlinePlayer, ChatColor.RED + "Failed to renew. You don't have enough money, please get more then manually resubscribe");
      } else {
        //Payment success
        //Update that the subscription is paid
        subscription.registerPaid();
        try {
          //Update the subscription in the database
          DatabaseHelper.getInstance().getDatabaseManager().getSubscriptionDao().update(subscription);

          //Pay and update the account
          service.getAccount().getAccount().addBalance(service.getCost());

          //Update the account in the database and add a log
          DatabaseHelper.getInstance().getDatabaseManager().updateAccount(service.getAccount().getAccount());
          DatabaseHelper.getInstance().getDatabaseManager().logTransaction(new Transaction(service.getAccount(),
              player.getUniqueId(), "Subscription payment for " + service.getName(), service.getCost()));

          if (onlinePlayer != null) {
            sendMessage(onlinePlayer, ChatColor.GREEN +
                "Success! Your subscription will be auto-renewed again in " + ChatColor.YELLOW + service.getDuration()
                + ChatColor.GREEN + " days time. You can cancel at any time by doing /stonks subscriptions");
          } else {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mail send " +
                player.getName() + " you have been billed $" + service.getCost() + " for the service " + service.getName());
          }
          System.out.println("(success)");
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void cancelSubscriptionIfOverdue(Subscription subscription) {
    Service service = subscription.getService();
    OfflinePlayer player = Bukkit.getOfflinePlayer(subscription.getPlayerId());
    //If it is still overdue (they didnt pay it or failed to pay it)
    if (subscription.isOverdue()) {
      if (subscription.getDaysOverdue() > service.getDuration()) {
        //They have not paid their subscription in over the duration
        //Auto cancel it
        try {
          DatabaseHelper.getInstance().getDatabaseManager().getSubscriptionDao().delete(subscription);
          Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mail send " +
              player.getName() + " Your subscription to " + service.getName() + " has been cancelled since you did not pay: " +
              (subscription.isAutoPay() ? " you did not have enough money to automatically pay it." : " you did not manually renew it."));
        } catch (SQLException e) {
          e.printStackTrace();
          System.out.println("Failed to delete subscription");
        }
      }

    }
  }

  private void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }
}