package nl.tychovi.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import nl.tychovi.stonks.model.Company;
import nl.tychovi.stonks.model.CompanyAccount;
import nl.tychovi.stonks.model.HoldingsAccount;
import nl.tychovi.stonks.model.IAccountVisitor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HoldingsAccountGUI implements InventoryProvider {
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


    HoldingsAccount account;
    Company company;
    public HoldingsAccountGUI(HoldingsAccount account, Company company) {
        this.account = account;
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
        contents.set(1, 1, ClickableItem.empty(namedItem(Material.COBWEB, "No holdings yet")));
        int col = 1;
        double totalShare = account.getTotalShare();
        account.getHoldings().forEach(h->{
            ItemStack s = new ItemStack(Material.BOOK);
            ItemMeta meta = s.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "[$" + h.getBalance() + ", "+ (h.getShare() / totalShare) * 100 +"%]");
            List<String> lore = new ArrayList<>();
            lore.add(Bukkit.getOfflinePlayer( h.getPlayer_uuid()).getName());
            meta.setLore(lore);
            s.setItemMeta(meta);

            contents.set(1, col, ClickableItem.empty(s));
        });
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
