package nl.tychovi.stonks;

import net.milkbowl.vault.economy.Economy;
import nl.tychovi.stonks.Database.Company;
import nl.tychovi.stonks.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Stonks extends JavaPlugin {

  private List<SpigotModule> loadedModules = new ArrayList<>();
  public static Economy economy = null;

  public static List<Company> companies = new ArrayList<>();

  @Override
  public void onEnable() {
    this.saveDefaultConfig();
    if(getConfig().getString("mysql.database").equals("YOUR-DATABASE")) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] It seems like you haven't set up your database in the config.yml yet, disabling plugin.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    loadedModules.add(new DatabaseManager(this));
    loadedModules.add(new ShopManager(this));
    loadedModules.add(new MessageManager(this));
    loadedModules.add(new GuiManager(this));

    if(!setupEconomy()) { return; }

    for (SpigotModule module : loadedModules) {
      module.onEnable();
    }
  }

  @Override
  public void onDisable() {
    for (SpigotModule module : loadedModules) {
      module.onDisable();
    }
  }

  public SpigotModule getModule(String name) {
    for(SpigotModule module : loadedModules) {
      if(module.getModuleName().equals(name)) {
        return module;
      }
    }
    return null;
  }

  private boolean setupEconomy()
  {
    RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    if (economyProvider != null) {
      economy = economyProvider.getProvider();
    }

    return (economy != null);
  }
}
