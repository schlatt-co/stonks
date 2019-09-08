package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Company;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CompanyListGui extends CollectionGuiBase<Company> {
  public CompanyListGui(Collection<Company> companies) {
    super(companies, "Company List");
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {

  }

  @Override
  protected ClickableItem itemProvider(Player player, Company obj) {
    return ClickableItem.of(ItemInfoHelper.companyDisplayItem(obj),
        e -> {
          CompanyInfoGui.getInventory(obj).open(player);
          player.performCommand("stonks info " + obj.getName());
        });
  }
}
