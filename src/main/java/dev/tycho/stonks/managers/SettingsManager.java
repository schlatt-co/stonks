package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;

//todo sort out spigot module stuff
public class SettingsManager extends SpigotModule {
  private static SettingsManager instance;

  public static double COMPANY_FEE = 0;
  public static double ACCOUNT_FEE = 0;
  public static long COMPANY_CREATION_COOLDOWN = 0;
  public static long ACCOUNT_CREATION_COOLDOWN = 0;

  SettingsManager(Stonks plugin) {
    super("Settings Manager", plugin);
    instance = this;
    COMPANY_FEE = plugin.getConfig().getDouble("fees.companycreation");
    ACCOUNT_FEE = plugin.getConfig().getDouble("fees.companyaccountcreation");
    COMPANY_CREATION_COOLDOWN = plugin.getConfig().getLong("cooldowns.companycreation");
    ACCOUNT_CREATION_COOLDOWN = plugin.getConfig().getLong("cooldowns.accountcreation");
  }

  public static SettingsManager getInstance() {
    return instance;
  }

}
