package dev.tycho.stonks.gui;

import dev.tycho.stonks.logging.Transaction;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.AccountLink;
import dev.tycho.stonks.model.CompanyAccount;
import dev.tycho.stonks.model.HoldingsAccount;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
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

import java.util.Collection;

public class TransactionHistoryGui implements InventoryProvider {

  public static DatabaseManager databaseManager;
  public static InventoryManager inventoryManager;

  private AccountLink accountLink;
  private Collection<Transaction> transactions;
  private String title;
  private boolean verbose;

  private SmartInventory inventory;

  private SmartInventory getInventory() {
    return SmartInventory.builder()
        .id("AccountSelectorGui")
        .provider(this)
        .manager(inventoryManager)
        .size(5, 9)
        .title(title)
        .build();
  }

  public TransactionHistoryGui(AccountLink accountLink, String title, Player player, boolean verbose) {
    this.accountLink = accountLink;
    this.verbose = verbose;
    this.title = title;
    this.inventory = getInventory();
    this.transactions = databaseManager.getTransactionDao()
        .getTransactionsForAccount(accountLink, databaseManager.getAccountLinkDao().queryBuilder(), 100, 0);
    inventory.open(player);
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to accounts"), e -> player.performCommand("stonks accounts " + accountLink.getCompany().getName())));

    IAccountVisitor visitor = new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        contents.set(0, 4, ClickableItem.empty(Util.item(Material.DIAMOND, a.getName())));
      }

      @Override
      public void visit(HoldingsAccount a) {
        contents.set(0, 4, ClickableItem.empty(Util.item(Material.GOLD_INGOT, a.getName())));
      }
    };
    accountLink.getAccount().accept(visitor);


    Pagination pagination = contents.pagination();

    ClickableItem[] items = new ClickableItem[transactions.size()];
    int i = 0;
    for (Transaction transaction : transactions) {
      ClickableItem item = ClickableItem.empty(ItemInfoHelper.transactionDisplayItem(transaction));
      items[i] = item;
      i++;
    }

    pagination.setItems(items);
    pagination.setItemsPerPage(27);

    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
    contents.set(4, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
        e -> getInventory().open(player, pagination.previous().getPage())));
    contents.set(4, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
        e -> getInventory().open(player, pagination.next().getPage())));
  }

  @Override
  public void update(Player player, InventoryContents contents) {

  }

  public static class Builder {
    private AccountLink accountLink;
    private String title = "";
    private boolean verbose = false;


    public Builder() {
    }

    public TransactionHistoryGui.Builder accountLink(AccountLink accountLink) {
      this.accountLink = accountLink;
      return this;
    }
    public TransactionHistoryGui.Builder title(String title) {
      this.title = title;
      return this;
    }

    public TransactionHistoryGui.Builder verbose() {
      this.verbose = true;
      return this;
    }

    public TransactionHistoryGui open(Player player) {
      return new TransactionHistoryGui(accountLink, title, player, verbose);
    }
  }


}