package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CompanyListGui extends CollectionGuiBase<Company> {
  public CompanyListGui(Collection<Company> companies) {
    super(companies, "Company List");
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.GRAY_STAINED_GLASS, "Filter by: Default (hide some companies)"), e -> player.performCommand("stonks list")));
    contents.set(0, 1, ClickableItem.of(Util.playerHead("Filter by: Member Of", player), e -> player.performCommand("stonks list member-of")));
    contents.set(0, 2, ClickableItem.of(Util.item(Material.ENCHANTED_BOOK, "Filter by: Verified"), e -> player.performCommand("stonks list verified")));
    contents.set(0, 3, ClickableItem.of(Util.item(Material.GLASS, "Filter by: Show All (incl. hidden)"), e -> player.performCommand("stonks list all")));
  }

  @Override
  protected ClickableItem itemProvider(Player player, Company obj) {
    return ClickableItem.of(ItemInfoHelper.companyDisplayItem(obj),
        e -> {
          CompanyInfoGui.getInventory(obj).open(player);
          player.performCommand("stonks info " + obj.name);
        });
  }
}
