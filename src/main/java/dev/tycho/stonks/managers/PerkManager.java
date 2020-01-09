package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.perks.ChestShopPerk;
import dev.tycho.stonks.util.Util;

import java.util.HashMap;

public class PerkManager extends SpigotModule {

  private static PerkManager instance;

  private final HashMap<String, CompanyPerk> registeredPerks = new HashMap<>();

  public PerkManager(Stonks plugin) {
    super("Perk Manager", plugin);
    instance = this;
    registerPerk(new ChestShopPerk(plugin));
  }

  public void registerPerk(CompanyPerk perk) {
    if (!Util.isCalledInternally()) {
      throw new RuntimeException("Improper use of internal stonks classes.");
    }
    registeredPerks.put(perk.getNamespace(), perk);
  }

  public void awardPerk(Company company, CompanyPerk perk) {
    Repo.getInstance().createPerk(company, perk.getNamespace());
  }

  public HashMap<String, CompanyPerk> getRegisteredPerks() {
    return registeredPerks;
  }

  public static PerkManager getInstance() {
    if (!Util.isCalledInternally()) {
      throw new RuntimeException("Improper use of internal stonks classes.");
    }
    return instance;
  }
}
