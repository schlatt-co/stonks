package dev.tycho.stonks.gui;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class InventoryGui implements InventoryProvider {
  public static InventoryManager inventoryManager;
  private SmartInventory inventory;

  InventoryGui(String title, int rows) {
    this.inventory =
        SmartInventory.builder()
            .id(title)
            .provider(this)
            .manager(inventoryManager)
            .size(rows, 9)
            .title(title)
            .build();
  }

  public Inventory show(Player player) {
    return inventory.open(player);
  }

  void close(Player player) {
    inventory.close(player);
  }

  SmartInventory getInventory() {
    return inventory;
  }

  @Override
  public abstract void init(Player player, InventoryContents contents);

  @Override
  public void update(Player player, InventoryContents contents) {

  }
}
