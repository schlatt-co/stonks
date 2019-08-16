package nl.tychovi.stonks.gui;

import org.bukkit.Material;

public class ExampleGui extends Gui {

    public ExampleGui() {
        super(9, "Example");
    }

    public void initializeItems() {
        inv.clear();
        inv.addItem(createGuiItem(Material.DIAMOND_SWORD, "Example Sword", "§aFirst line of the lore", "§bSecond line of the lore"));
        inv.addItem(createGuiItem(Material.IRON_HELMET, "§bExample Helmet", "§aFirst line of the lore", "§bSecond line of the lore"));
    }
}