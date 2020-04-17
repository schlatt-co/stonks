package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.core.Role;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CompanyInfoGui extends InventoryGui {

  public static InventoryManager inventoryManager;

  private Company company;

  public CompanyInfoGui(Company company) {
    super(company.name, 5);
    this.company = company;
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    contents.fillBorders(ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Company List"),
        e -> player.performCommand("stonks list")));
    contents.set(0, 4, ClickableItem.empty(ItemInfoHelper.companyDisplayItem(company)));


    contents.set(2, 3, ClickableItem.of(Util.item(Material.PLAYER_HEAD, "Members"),
        e -> player.performCommand("stonks members " + company.name)));
    contents.set(2, 4, ClickableItem.of(Util.item(Material.GOLD_BLOCK, "Accounts"),
        e -> player.performCommand("stonks accounts " + company.name)));
    contents.set(2, 5, ClickableItem.of(Util.item(Material.BOOKSHELF, "Services"),
        e -> player.performCommand("stonks servicefolders " + company.name)));

    Member member = company.getMember(player);
    if (member != null && member.role != Role.Intern) {
      contents.set(3, 4, ClickableItem.of(Util.item(Material.DIAMOND, "Perks"),
          e -> player.performCommand("stonks perks " + company.name)));
    }

    if (player.hasPermission("trevor.mod")) {
      if (company.verified) {
        contents.set(0, 7, ClickableItem.of(Util.item(Material.BOOK, "Unverify"),
            e -> {
              player.performCommand("stonks unverify " + company.pk);
              player.performCommand("stonks list");
            }));
      } else {
        contents.set(0, 7, ClickableItem.of(Util.item(Material.ENCHANTED_BOOK, "Verify"),
            e -> {
              player.performCommand("stonks verify " + company.pk);
              player.performCommand("stonks list");
            }));
      }

      if (company.hidden) {
        contents.set(0, 8, ClickableItem.of(Util.item(Material.GREEN_STAINED_GLASS_PANE, "Unhide"),
            e -> {
              player.performCommand("stonks unhide " + company.pk);
              player.performCommand("stonks list");
            }));
      } else {
        contents.set(0, 8, ClickableItem.of(Util.item(Material.RED_STAINED_GLASS_PANE, "Hide"),
            e -> {
              player.performCommand("stonks hide " + company.pk);
              player.performCommand("stonks list");
            }));
      }

      contents.set(4, 8, ClickableItem.of(Util.item(Material.RED_CONCRETE, "[!] DELETE [!]"),
          e -> {
            player.performCommand("stonks delete " + company.pk);
            player.performCommand("stonks list");
          }));


    }
  }
}
