package nl.tychovi.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import nl.tychovi.stonks.model.Company;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompanyGUI implements InventoryProvider {

  private ItemStack namedItem(ItemStack s, String name) {
    ItemMeta meta = s.getItemMeta();
    meta.setDisplayName(name);
    s.setItemMeta(meta);
    return s;
  }

  private ItemStack namedItem(Material m, String name) {
    ItemStack s = new ItemStack(m);
    ItemMeta meta = s.getItemMeta();
    meta.setDisplayName(name);
    s.setItemMeta(meta);
    return s;
  }

  Company company;

  public CompanyGUI(Company company) {
    this.company = company;
  }


  @Override
  public void init(Player player, InventoryContents contents) {
    contents.fillBorders(ClickableItem.empty(namedItem(Material.GRAY_STAINED_GLASS_PANE, " ")));
    contents.set(0, 4, ClickableItem.empty(namedItem(Material.GOLD_BLOCK, company.getName())));
    contents.set(1, 3, ClickableItem.empty(namedItem(Material.PLAYER_HEAD, "Members")));
    contents.set(1, 4, ClickableItem.of(namedItem(Material.GOLD_INGOT, "Accounts"),
        (e) -> {
          SmartInventory inv = SmartInventory.builder()
              .provider(new AccountsViewGUI(company))
              .manager(contents.inventory().getManager())
              .title(ChatColor.YELLOW + "Accounts View")
              .parent(contents.inventory())
              .build();
          inv.open(player);
        }
    ));
    contents.set(1, 5, ClickableItem.empty(namedItem(Material.PAPER, "Details")));
  }

  @Override
  public void update(Player player, InventoryContents inventoryContents) {

  }
}
