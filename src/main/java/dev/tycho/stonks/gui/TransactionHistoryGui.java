package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.CompanyAccount;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TransactionHistoryGui extends CollectionGui<Transaction> {
  private Account account;

  private TransactionHistoryGui(Account account, String title) {
    super(account.transactions, title);
    this.account = account;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to accounts"),
        e -> player.performCommand("stonks accounts " + account.companyPk)));
    IAccountVisitor visitor = new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        contents.set(0, 4, ClickableItem.empty(Util.item(Material.DIAMOND, a.name)));
      }

      @Override
      public void visit(HoldingsAccount a) {
        contents.set(0, 4, ClickableItem.empty(Util.item(Material.GOLD_INGOT, a.name)));
      }
    };
    account.accept(visitor);
  }

  @Override
  protected ClickableItem itemProvider(Player player, Transaction obj) {
    return ClickableItem.empty(ItemInfoHelper.transactionDisplayItem(obj));
  }

  public static class Builder {
    private Account account;
    private String title = "";


    public Builder() {
    }

    public TransactionHistoryGui.Builder account(Account account) {
      this.account = account;
      return this;
    }

    public TransactionHistoryGui.Builder title(String title) {
      this.title = title;
      return this;
    }

    public TransactionHistoryGui open(Player player) {
      TransactionHistoryGui transactionHistoryGui = new TransactionHistoryGui(account, title);
      transactionHistoryGui.show(player);
      return transactionHistoryGui;
    }
  }


}