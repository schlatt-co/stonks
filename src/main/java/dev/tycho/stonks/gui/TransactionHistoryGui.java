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

import java.util.Collection;

public class TransactionHistoryGui extends CollectionGui<Transaction> {
  private Account account;

  public TransactionHistoryGui(Collection<Transaction> transactions, Account account) {
    super(transactions, "Transaction History");
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
}