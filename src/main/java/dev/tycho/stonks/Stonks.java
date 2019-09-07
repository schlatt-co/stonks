package dev.tycho.stonks;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import dev.tycho.stonks.command.TabCompleterCompany;
import dev.tycho.stonks.managers.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Stonks extends JavaPlugin {

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

    loadedModules.add(new DatabaseManager(this));
    loadedModules.add(new ShopManager(this));
    loadedModules.add(new MessageManager(this));
    loadedModules.add(new GuiManager(this));
//    loadedModules.add(new SignManager(this));

    if (!setupEconomy()) {
      return;
    }

    for (SpigotModule module : loadedModules) {
      module.onEnable();
    }

    getCommand("company").setTabCompleter(new TabCompleterCompany());
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
}
