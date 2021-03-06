package dev.tycho.stonks;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.earth2me.essentials.Essentials;
import dev.tycho.stonks.command.base.CommandBase;
import dev.tycho.stonks.managers.*;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import dev.tycho.stonks.util.BukkitUser;
import dev.tycho.stonks.util.EssentialsUser;
import dev.tycho.stonks.util.StonksUser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Stonks extends JavaPlugin {

  public static Economy economy = null;
  private static Stonks instance;
  private static Essentials essentials = null;
  private static TaskChainFactory taskChainFactory;
  private List<SpigotModule> loadedModules = new ArrayList<>();

  public static <T> TaskChain<T> newChain() {
    return taskChainFactory.newChain();
  }

  // Here for maybe future use
  public static <T> TaskChain<T> newSharedChain(String name) {
    return taskChainFactory.newSharedChain(name);
  }

  public static StonksUser getUser(UUID uuid) {
    if (uuid == null) return null;
    if (essentials != null) {
      return new EssentialsUser(essentials.getUser(uuid));
    } else {
      return new BukkitUser(Bukkit.getPlayer(uuid));
    }
  }

  public static StonksUser getOfflineUser(String name) {
    if (essentials != null) {
      return new EssentialsUser(essentials.getOfflineUser(name));
    } else {
      //noinspection deprecation - bukkit is stupid
      return new BukkitUser(Bukkit.getOfflinePlayer(name));
    }
  }

  public static Stonks getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    instance = this;
    CommandBase.setPlugin(this);
    if (Objects.equals(getConfig().getString("mysql.database"), "YOUR-DATABASE")) {
      Bukkit.getLogger().severe("It seems like you haven't set up your database in the config.yml yet, disabling plugin.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    taskChainFactory = BukkitTaskChainFactory.create(this);
    loadedModules.add(new Repo(this));
    loadedModules.add(new MessageManager(this));
    loadedModules.add(new GuiManager(this));
    loadedModules.add(new SettingsManager(this));
    loadedModules.add(new PerkManager(this));
    loadedModules.add(new CommandManager(this));
    if (getServer().getPluginManager().getPlugin("ChestShop") != null) {
      loadedModules.add(new ShopManager(this));
    }
    if (!setupEconomy()) {
      return;
    }
    if (this.getServer().getPluginManager().getPlugin("Essentials") != null) {
      if (!setupEssentials()) {
        return;
      }
    }
    for (SpigotModule module : loadedModules) {
      module.onEnable();
    }

    getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
      System.out.println("Automatically renewing subscriptions");
      //We want to go through all subscriptions

      List<Subscription> subscriptions = Repo.getInstance().subscriptions().getAll();
      for (Subscription subscription : subscriptions) {
        renewSubscription(subscription);
        cancelSubscriptionIfOverdue(subscription);
      }
    }, 0, 6000);
    findDefecits();
    Bukkit.getLogger().info("Loaded!");
  }

  private void findDefecits() {
    for (Company company : Repo.getInstance().companies().getAll()) {
      for (Account account : company.accounts) {
        double total = 0;
        Collection<Transaction> transactions = Repo.getInstance().transactions().getTransactionsForAccount(account);
        for (Transaction transaction : transactions) {
          total += transaction.amount;
        }
        if (Math.abs(total - account.getTotalBalance()) > 2) {
          System.out.println("Company " + company.name + ", account " + account.pk + " " + account.name + " defecit of $"
              + (total - account.getTotalBalance()));
        }
      }
    }
  }

  private void renewSubscription(Subscription subscription) {
    Service service = Repo.getInstance().services().get(subscription.servicePk);

    OfflinePlayer player = Bukkit.getOfflinePlayer(subscription.playerUUID);
    Player onlinePlayer = null;
    if (player.isOnline()) onlinePlayer = player.getPlayer();

    if (Subscription.isOverdue(service, subscription)) {
      //Subscription is overdue and we must renew it
      if (onlinePlayer != null)
        sendMessage(onlinePlayer, ChatColor.YELLOW + "Your subscription for " + service.name + " has ended." + ChatColor.GREEN + " Automatically renewing it now.....");

      //Try and charge them the amount due
      if (!Stonks.economy.withdrawPlayer(player, service.cost).transactionSuccess()) {
        //Player does not have enough money
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

  @Override
  public void onDisable() {
    for (SpigotModule module : loadedModules) {
      module.onDisable();
    }
  }

  private boolean setupEconomy() {
    RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    if (economyProvider != null) {
      economy = economyProvider.getProvider();
    }
    return (economy != null);
  }

  private boolean setupEssentials() {
    essentials = (Essentials) this.getServer().getPluginManager().getPlugin("Essentials");
    return (essentials != null);
  }
}
