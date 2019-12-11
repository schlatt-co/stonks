package dev.tycho.stonks;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.earth2me.essentials.Essentials;
import dev.tycho.stonks.command.MainCommand;
import dev.tycho.stonks.managers.*;
import dev.tycho.stonks.scheduledtasks.SubscriptionCheckTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class Stonks extends JavaPlugin {

  public static Essentials essentials = null;
  public static Economy economy = null;
  private static TaskChainFactory taskChainFactory;
  private List<SpigotModule> loadedModules = new ArrayList<>();

  public static <T> TaskChain<T> newChain() {
    return taskChainFactory.newChain();
  }
//  Here for maybe future use
//  public static <T> TaskChain<T> newSharedChain(String name) { return taskChainFactory.newSharedChain(name); }

  @Override
  public void onEnable() {
    if (getConfig().getString("mysql.database").equals("YOUR-DATABASE")) {
      Bukkit.getLogger().severe("It seems like you haven't set up your database in the config.yml yet, disabling plugin.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    taskChainFactory = BukkitTaskChainFactory.create(this);
    loadedModules.add(new Repo(this));
    loadedModules.add(new ShopManager(this));
    loadedModules.add(new MessageManager(this));
    loadedModules.add(new GuiManager(this));
    loadedModules.add(new SettingsManager(this));
    if (!setupEconomy()) {
      return;
    }
    if (!setupEssentials()) {
      return;
    }
    for (SpigotModule module : loadedModules) {
      module.onEnable();
    }

//    Schedule the auto-pay services task
    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, new SubscriptionCheckTask(), 1L, 20L * SettingsManager.SUBSCRIPTION_AUTOPAY_TASK_INTERVAL);

    MainCommand command = new MainCommand();
    getCommand("company").setTabCompleter(command);
    getCommand("company").setExecutor(command);
    Bukkit.getLogger().info("Loaded!");
  }

  @Override
  public void onDisable() {
    for (SpigotModule module : loadedModules) {
      module.onDisable();
    }
  }

  public SpigotModule getModule(String name) {
    for (SpigotModule module : loadedModules) {
      if (module.getModuleName().equals(name)) {
        return module;
      }
    }
    return null;
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
