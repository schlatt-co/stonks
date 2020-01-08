package dev.tycho.stonks.api.perks;

import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public abstract class CompanyPerk {

  private final Plugin plugin;
  private final String name;
  private final Material icon;
  private final int price;
  private final String[] description;
  private final CompanyPerkAction[] perkActions;

  public CompanyPerk(Plugin plugin, String name, Material icon, int price, String... description) {
    this(plugin, name, icon, price, description, new CompanyPerkAction[]{});
  }

  public CompanyPerk(Plugin plugin, String name, Material icon, int price, String[] description, CompanyPerkAction... companyPerkActions) {
    this.plugin = plugin;
    this.name = name;
    this.icon = icon;
    this.price = price;
    this.description = description;
    this.perkActions = companyPerkActions;
  }

  public abstract void onPurchase(Company company, Member purchaser);

  public Plugin getPlugin() {
    return plugin;
  }

  public final String getName() {
    return name;
  }

  public final Material getIcon() {
    return icon;
  }

  public final int getPrice() {
    return price;
  }

  public final String[] getDescription() {
    return description;
  }

  public final CompanyPerkAction[] getPerkActions() {
    return perkActions;
  }

  public final String getNamespace() {
    return getPlugin().getName().toLowerCase() + ":" + getName().toLowerCase();
  }
}
