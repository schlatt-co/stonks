package dev.tycho.stonks.gui;

import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.Account;
import dev.tycho.stonks.model.AccountLink;
import dev.tycho.stonks.model.Company;
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

import java.util.function.Consumer;

public class AccountSelectorGui extends CollectionGuiBase<AccountLink> {

  private Company company;
  private Consumer<AccountLink> onAccountSelected;

  public AccountSelectorGui(Company company, String title, Consumer<AccountLink> onAccountSelected, Player player) {
    super(company.getAccounts(), title);
    this.company = company;
    this.onAccountSelected = onAccountSelected;
    show(player);
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));
  }

  @Override
  protected ClickableItem itemProvider(Player player, AccountLink obj) {
    return ClickableItem.of(ItemInfoHelper.accountDisplayItem(obj),
        e -> {
            onAccountSelected.accept(obj);
            close(player);
        });
  }

  public static class Builder {
    private Company company = null;
    private String title = "";
    private Consumer<AccountLink> onAccountSelected;

    public Builder() {

    }

    public AccountSelectorGui.Builder company(Company company) {
      this.company = company;
      return this;
    }

    public AccountSelectorGui.Builder title(String title) {
      this.title = title;
      return this;
    }

    public AccountSelectorGui.Builder accountSelected(Consumer<AccountLink> onAccountSelected) {
      this.onAccountSelected = onAccountSelected;
      return this;
    }

    public AccountSelectorGui open(Player player) {
      return new AccountSelectorGui(company, title, onAccountSelected, player);
    }
  }


}
