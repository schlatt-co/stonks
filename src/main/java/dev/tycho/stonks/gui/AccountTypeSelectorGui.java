package dev.tycho.stonks.gui;

import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class AccountTypeSelectorGui extends InventoryGui {
  private Consumer<AccountType> onTypeSelected;

  public AccountTypeSelectorGui(Consumer<AccountType> onTypeSelected, String title) {
    super(title);
    this.onTypeSelected = onTypeSelected;
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    Pagination pagination = contents.pagination();


    contents.set(1, 3, ClickableItem.of(Util.item(Material.DIAMOND, "Company Account"),
        e -> {
          close(player);
          onTypeSelected.accept(AccountType.CompanyAccount);
        }));
    contents.set(1, 5, ClickableItem.of(Util.item(Material.GOLD_INGOT, "Holdings Account"),
        e -> {
          close(player);
          onTypeSelected.accept(AccountType.HoldingsAccount);
        }));
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

    public void show(Player player) {
      new AccountTypeSelectorGui(onTypeSelected, title).show(player);
    }
  }
}
