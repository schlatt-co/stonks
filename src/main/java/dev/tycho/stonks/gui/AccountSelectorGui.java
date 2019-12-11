package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class AccountSelectorGui extends CollectionGui<Account> {

  private Company company;
  private Consumer<Account> onAccountSelected;

  private AccountSelectorGui(Company company, String title, Consumer<Account> onAccountSelected, Player player) {
    super(company.accounts, title);
    this.company = company;
    this.onAccountSelected = onAccountSelected;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.logoMaterial), company.name)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, Account obj) {
    return ClickableItem.of(ItemInfoHelper.accountDisplayItem(obj, player),
        e -> {
          onAccountSelected.accept(obj);
          close(player);
        });
  }

  public static class Builder {
    private Company company = null;
    private String title = "";
    private Consumer<Account> onAccountSelected;

    public AccountSelectorGui.Builder company(Company company) {
      this.company = company;
      return this;
    }

    public AccountSelectorGui.Builder title(String title) {
      this.title = title;
      return this;
    }

    public AccountSelectorGui.Builder accountSelected(Consumer<Account> onAccountSelected) {
      this.onAccountSelected = onAccountSelected;
      return this;
    }

    public void show(Player player) {
      new AccountSelectorGui(company, title, onAccountSelected, player).show(player);
    }
  }


}
