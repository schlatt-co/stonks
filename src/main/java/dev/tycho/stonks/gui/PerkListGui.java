package dev.tycho.stonks.gui;

import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.managers.PerkManager;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PerkListGui extends CollectionGui<CompanyPerk> {

  private Company company;

  public PerkListGui(Company company) {
    super(PerkManager.getInstance().getRegisteredPerks().values(), company.name + "'s Perks");
    this.company = company;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"),
        e -> player.performCommand("stonks info " + company.name)));
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.logoMaterial),
        company.name)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, CompanyPerk obj) {
    return ClickableItem.empty(ItemInfoHelper.perkDisplayItem(company, obj));
  }
}
