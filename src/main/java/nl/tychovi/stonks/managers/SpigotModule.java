package nl.tychovi.stonks.managers;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SpigotModule implements Listener {

  private final String moduleName;
  final JavaPlugin plugin;

  SpigotModule(String moduleName, JavaPlugin plugin) {
    this.moduleName = moduleName;
    this.plugin = plugin;
  }

  public final void onEnable() {
    long epoch = System.currentTimeMillis();
    log("Initializing...");
    enable();
    addCommands();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    log("Enabled in " + (System.currentTimeMillis() - epoch) + " milliseconds.");
  }

  public final void onDisable() {
    disable();
    log("Disabled");
  }

  @SuppressWarnings({"ConstantConditions", "SameParameterValue"})
  final void addCommand(String name, CommandExecutor commandExecutor) {
    plugin.getCommand(name).setExecutor(commandExecutor);
  }

  public void enable() {

  }

  public void disable() {

  }

  public void addCommands() {

  }

  void log(String message) {
    System.out.println("[CowBot] " + moduleName + "> " + message);
  }
}
