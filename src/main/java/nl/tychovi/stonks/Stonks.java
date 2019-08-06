package nl.tychovi.stonks;

import nl.tychovi.stonks.managers.DatabaseManager;
import nl.tychovi.stonks.managers.ShopManager;
import nl.tychovi.stonks.managers.SpigotModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Stonks extends JavaPlugin {

  private List<SpigotModule> loadedModules = new ArrayList<>();

  @Override
  public void onEnable() {
    getConfig().addDefault("mysql.username", "");
    getConfig().addDefault("mysql.password", "");
    getConfig().options().copyDefaults(true);
    saveConfig();
    reloadConfig();

    loadedModules.add(new DatabaseManager(this));
    loadedModules.add(new ShopManager(this));

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
}
