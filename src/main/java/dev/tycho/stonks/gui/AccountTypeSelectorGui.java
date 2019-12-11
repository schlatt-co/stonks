package dev.tycho.stonks.gui;

import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class AccountTypeSelectorGui implements InventoryProvider {
  public static InventoryManager inventoryManager;

  private SmartInventory inventory;
  private Consumer<AccountType> onTypeSelected;

  public AccountTypeSelectorGui(Consumer<AccountType> onTypeSelected, String title, Player player) {
    this.onTypeSelected = onTypeSelected;
    this.inventory = SmartInventory.builder()
        .id("CompanySelectorGui")
        .provider(this)
        .manager(inventoryManager)
        .size(3, 9)
        .title(title)
        .build();
    inventory.open(player);
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    Pagination pagination = contents.pagination();


    contents.set(1, 3, ClickableItem.of(Util.item(Material.DIAMOND, "Company Account"),
        e -> {
          inventory.close(player);
          onTypeSelected.accept(AccountType.CompanyAccount);
        }));
    contents.set(1, 5, ClickableItem.of(Util.item(Material.GOLD_INGOT, "Holdings Account"),
        e -> {
          inventory.close(player);
          onTypeSelected.accept(AccountType.HoldingsAccount);
        }));
  }

  @Override
  public void update(Player player, InventoryContents contents) {

  }

  public enum AccountType {
    CompanyAccount,
    HoldingsAccount
  }

  public static class Builder {
    private String title = "";
    private Consumer<AccountType> onTypeSelected;

    public Builder() {

    }

    public AccountTypeSelectorGui.Builder typeSelected(Consumer<AccountType> onTypeSelected) {
      this.onTypeSelected = onTypeSelected;
      return this;
    }

    public AccountTypeSelectorGui.Builder title(String title) {
      this.title = title;
      return this;
    }

    public AccountTypeSelectorGui open(Player player) {
      return new AccountTypeSelectorGui(onTypeSelected, title, player);
    }
  }
}
