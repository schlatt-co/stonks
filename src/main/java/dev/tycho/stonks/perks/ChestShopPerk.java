package dev.tycho.stonks.perks;

import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class ChestShopPerk extends CompanyPerk {

  public ChestShopPerk(Plugin plugin) {
    super(plugin, "ChestShop Integration", Material.CHEST, 4000, "This perk allows you to create a ChestShop",
        "as a company! Allowing direct deposit/withdrawal into",
        "company accounts; As well as sharing locked chests",
        "among company members. Once purchased, all you need to",
        "do is use a hashtag followed by an account number",
        "on the first line of the shop sign. For example,",
        "if you had the account number `1` you'd type `#1` on",
        "the first line of the sign.");
  }

  @Override
  public void onPurchase(Company company, Member purchaser) {

  }
}
