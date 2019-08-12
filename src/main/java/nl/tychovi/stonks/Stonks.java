package nl.tychovi.stonks;

import net.milkbowl.vault.economy.Economy;
import nl.tychovi.stonks.managers.DatabaseManager;
import nl.tychovi.stonks.managers.DatabaseManagerV2;
import nl.tychovi.stonks.managers.ShopManager;
import nl.tychovi.stonks.managers.SpigotModule;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Stonks extends JavaPlugin {

  private List<SpigotModule> loadedModules = new ArrayList<>();
  public static Economy economy = null;

  @Override
  public void onEnable() {
    this.saveDefaultConfig();

    loadedModules.add(new DatabaseManagerV2(this));
    loadedModules.add(new ShopManager(this));

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
