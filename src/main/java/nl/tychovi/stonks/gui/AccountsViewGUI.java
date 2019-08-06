package nl.tychovi.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import nl.tychovi.stonks.model.Company;
import nl.tychovi.stonks.model.CompanyAccount;
import nl.tychovi.stonks.model.HoldingsAccount;
import nl.tychovi.stonks.model.IAccountVisitor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AccountsViewGUI implements InventoryProvider {
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

  public AccountsViewGUI(Company company) {
    this.company = company;
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    //Add a border
    contents.fillBorders(ClickableItem.empty(namedItem(Material.GRAY_STAINED_GLASS_PANE, " ")));
    //Add a gold block
    contents.set(0, 4, ClickableItem.of(namedItem(Material.GOLD_BLOCK, company.getName()), (e) -> {
      //When the gold block is clicked, return to the company view
      if (contents.inventory().getParent().isPresent()) {
        SmartInventory inv = SmartInventory.builder()
            .provider(new CompanyGUI(company))
            .manager(contents.inventory().getManager())
            .title(ChatColor.YELLOW + "Company View")
            .build();
        inv.open(player);
      }
    }));

    //Add a placeholder for no accounts found
    contents.set(1, 1, ClickableItem.empty(namedItem(Material.COBWEB, "No accounts")));

    final int[] col = {1};
    //For each account add an item to inspect
    company.getAccounts().forEach(a -> {
      IAccountVisitor visitor = new IAccountVisitor() {
        @Override
        public void visit(HoldingsAccount a) {
          contents.set(1, col[0], ClickableItem.of(namedItem(Material.GOLD_NUGGET,
              a.getName() + ChatColor.GOLD + "[click to see holdings]"
          ), (e) -> {
            SmartInventory inv = SmartInventory.builder()
                .provider(new HoldingsAccountGUI(a, company))
                .parent(contents.inventory())
                .manager(contents.inventory().getManager())
                .title(ChatColor.YELLOW + "Company View")
                .build();
            inv.open(player);
          }));
          col[0]++;
        }

        @Override
        public void visit(CompanyAccount a) {
          contents.set(1, col[0], ClickableItem.empty(namedItem(Material.IRON_NUGGET,
              a.getName() + ChatColor.GOLD + "  $" + a.getBalance()
          )));
          col[0]++;
        }

        @Override
        public Object result() {
          return null;
        }
      };
      a.accept(visitor);
    });
  }

  @Override
  public void update(Player player, InventoryContents inventoryContents) {

  }
}
