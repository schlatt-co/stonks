package dev.tycho.stonks.scheduledtasks;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.CompanyAccount;
import dev.tycho.stonks.model.core.HoldingsAccount;
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
    Account account = service.getAccount().getAccount();
    //Refresh the account because we have to recurse quite deeply
    IAccountVisitor visitor = new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        try {
          DatabaseHelper.getInstance().getDatabaseManager().getCompanyAccountDao().refresh(a);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void visit(HoldingsAccount a) {
        try {
          DatabaseHelper.getInstance().getDatabaseManager().getHoldingsAccountDao().refresh(a);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }

    };
    account.accept(visitor);

    if (account.getName() == null) return;


    OfflinePlayer player = Bukkit.getOfflinePlayer(subscription.getPlayerId());
    Player onlinePlayer = null;
    if (player.isOnline()) onlinePlayer = player.getPlayer();

    if (subscription.isOverdue()) {
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
          //Pay the account
          account.addBalance(service.getCost());

          //Update the subscription in the database
          DatabaseHelper.getInstance().getDatabaseManager().getSubscriptionDao().update(subscription);

          //Update the account in the database and add a log
          DatabaseHelper.getInstance().getDatabaseManager().updateAccount(account);
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

  /*
  * IAccountVisitor visitor = new IAccountVisitor() {
            @Override
            public void visit(CompanyAccount a) {
              try {
                databaseManager.getCompanyAccountDao().update(a);
              } catch (SQLException e) {
                sendMessage(sender, "Error while executing command!");
                e.printStackTrace();
              }
            }

            @Override
            public void visit(HoldingsAccount a) {
              try {
                databaseManager.getHoldingAccountDao().update(a);
                for (Holding h : a.getHoldings()) {
                  databaseManager.getHoldingDao().update(h);
                }
              } catch (SQLException e) {
                sendMessage(sender, "Error while executing command!");
                e.printStackTrace();
              }
            }
          };
          accountLink.getAccount().accept(visitor);
  *
  * */

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
              player.getName() + " Your subscription to " + service.getName() +
              " has been cancelled since you did not have enough money to automatically pay it.");
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