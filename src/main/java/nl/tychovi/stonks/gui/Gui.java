package nl.tychovi.stonks.gui;

import nl.tychovi.stonks.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class Gui implements InventoryHolder, Listener {
    final Inventory inv;
    private String title;
    DatabaseManager databaseManager;

    public Gui(int size, String title, DatabaseManager databaseManager) {
        inv = Bukkit.createInventory(this, size, title);
        this.title = title;
        this.databaseManager = databaseManager;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public String getTitle() { return title; }

    public ItemStack createGuiItem(Material material, String name, String...lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metaLore = new ArrayList<String>();

        for(String loreComments : lore) {

            metaLore.add(loreComments);

        }

        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    public void openInventory(Player p) {
        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != this.getInventory()) {
            return;
        }
        if (e.getClick().equals(ClickType.NUMBER_KEY)){
            e.setCancelled(true);
        }
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
    }
}
