package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.perks.CompanyChatPerk;
import dev.tycho.stonks.util.Util;

import java.util.HashMap;

public class PerkManager extends SpigotModule {

  private static PerkManager instance;

  private final HashMap<String, CompanyPerk> registeredPerks = new HashMap<>();
  private final HashMap<String, String> classNamespaceMap = new HashMap<>();

  public PerkManager(Stonks plugin) {
    super("Perk Manager", plugin);
    instance = this;
    registerPerk(new CompanyChatPerk(plugin));
  }

  public void registerPerk(CompanyPerk perk) {
    if (!Util.isCalledInternally()) {
      throw new RuntimeException("Improper use of internal stonks classes.");
    }
    registeredPerks.put(perk.getNamespace(), perk);
    classNamespaceMap.put(perk.getClass().getName(), perk.getNamespace());
    log("Registered perk: " + perk.getNamespace());
  }

  public void awardPerk(Company company, CompanyPerk perk) {
    Repo.getInstance().createPerk(company, perk.getNamespace());
  }

  public HashMap<String, CompanyPerk> getRegisteredPerks() {
    return registeredPerks;
  }

  public HashMap<String, String> getClassNamespaceMap() {
    return classNamespaceMap;
  }

  public static PerkManager getInstance() {
    if (!Util.isCalledInternally()) {
      throw new RuntimeException("Improper use of internal stonks classes.");
    }
    return instance;
  }
}
