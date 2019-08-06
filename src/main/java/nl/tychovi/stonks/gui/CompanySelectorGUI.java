package nl.tychovi.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import nl.tychovi.stonks.managers.DatabaseManager;
import nl.tychovi.stonks.model.Company;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class CompanySelectorGUI implements InventoryProvider {
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

  private DatabaseManager databaseManager;
  private SmartInventory inventory;

  public CompanySelectorGUI(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    Pagination pagination = contents.pagination();

    ClickableItem[] items = new ClickableItem[databaseManager.getCachedCompanies().size()];
    for (int i = 0; i < items.length; i++) {
      Company c = databaseManager.getCachedCompanies().get(i);
      ItemStack item = new ItemStack(Material.PAPER);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(c.getName());
      item.setItemMeta(meta);
      items[i] = ClickableItem.of(item, e -> {
        SmartInventory inv = SmartInventory.builder()
            .provider(new CompanyGUI(c))
            .manager(contents.inventory().getManager())
            .title(ChatColor.YELLOW + "Company View")
            .parent(this.inventory)
            .build();
        inv.open(player);
      });
    }

    contents.fillBorders(ClickableItem.empty(namedItem(Material.GRAY_STAINED_GLASS_PANE, " ")));
    pagination.setItems(items);
    pagination.setItemsPerPage(2);
    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));

    SmartInventory inv = SmartInventory.builder()
        .provider(this)
        .manager(contents.inventory().getManager())
        .size(4, 9)
        .title(ChatColor.YELLOW + "Company Edit")
        .build();

    contents.set(2, 3, ClickableItem.of(new ItemStack(Material.ARROW),
        e -> inv.open(player, pagination.previous().getPage())));
    contents.set(2, 5, ClickableItem.of(new ItemStack(Material.ARROW),
        e -> inv.open(player, pagination.next().getPage())));

  }


  @Override
  public void update(Player player, InventoryContents contents) {
  }

}
