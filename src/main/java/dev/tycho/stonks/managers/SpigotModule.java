package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public abstract class SpigotModule implements Listener {

  private final String moduleName;
  final Stonks plugin;

  SpigotModule(String moduleName, Stonks plugin) {
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

  @SuppressWarnings({"SameParameterValue"})
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
    System.out.println("[Stonks] " + moduleName + "> " + message);
  }

  public String getModuleName() {
    return moduleName;
  }
}
