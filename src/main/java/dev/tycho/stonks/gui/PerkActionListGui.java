package dev.tycho.stonks.gui;

import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.api.perks.CompanyPerkAction;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PerkActionListGui extends CollectionGui<CompanyPerkAction> {

  private Company company;

  public PerkActionListGui(Company company, CompanyPerk perk) {
    super(Arrays.asList(perk.getPerkActions()), perk.getName() + "'s Actions");
    this.company = company;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to perks"),
        e -> player.performCommand("stonks perks " + company.name)));
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.logoMaterial),
        company.name)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, CompanyPerkAction obj) {
    return ClickableItem.of(ItemInfoHelper.perkActionDisplayItem(company, player, obj), e -> {
      if (company.getMember(player).role.hasPermission(obj.getPermissionLevel())) {
        obj.onExecute(company, player);
      } else {
        sendMessage(player, "You have insufficient permission to execute this perk action! Please either ask to be promoted " +
            "or have a higher ranking member execute it");
      }
      getInventory().close(player);
    });
  }
}
