package dev.tycho.stonks.gui;

import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.Collections;

public abstract class CollectionGuiBase<T> implements InventoryProvider {
  public static InventoryManager inventoryManager;
  private Collection<T> collection;
  private SmartInventory inventory;

  protected CollectionGuiBase(Collection<T> collection, String title) {
    this.collection = collection;
    this.inventory = SmartInventory.builder()
        .id(title)
        .provider(this)
        .manager(inventoryManager)
        .size(6, 9)
        .title(title)
        .build();
  }

  protected Collection<T> getReadonlyCollection() {
    return Collections.unmodifiableCollection(collection);
  }

  public Inventory show(Player player) {
    return inventory.open(player);
  }

  void close(Player player) {
    inventory.close(player);
  }

  private SmartInventory getInventory() {
    return inventory;
  }


  protected abstract void customInit(Player player, InventoryContents contents);

  protected abstract ClickableItem itemProvider(Player player, T obj);


  @Override
  public void init(Player player, InventoryContents contents) {
    contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.fillRow(5, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    customInit(player, contents);

    Pagination pagination = contents.pagination();
    ClickableItem[] items = new ClickableItem[collection.size()];
    int i = 0;
    for (T obj : collection) {
      items[i] = itemProvider(player, obj);
      i++;
    }
    pagination.setItems(items);
    pagination.setItemsPerPage(36);
    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

    contents.set(5, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
        e -> getInventory().open(player, pagination.previous().getPage())));
    contents.set(5, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
        e -> getInventory().open(player, pagination.next().getPage())));
  }

  @Override
  public void update(Player player, InventoryContents contents) {

  }
}
