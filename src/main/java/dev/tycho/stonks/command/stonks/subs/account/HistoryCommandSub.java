package dev.tycho.stonks.command.stonks.subs.account;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.gui.TransactionHistoryGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.logging.Transaction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HistoryCommandSub extends ModularCommandSub {

  public HistoryCommandSub() {
    super(new AccountValidator("account"));
  }

  @Override
  public void execute(Player player) {
    Account account = getArgument("account");
    if (account == null) {
      player.sendMessage(ChatColor.RED + "Account not found");
      return;
    }

    Stonks.newChain().asyncFirst(() -> {
      List<Transaction> transactions = new ArrayList<>(Repo.getInstance().transactions().getTransactionsForAccount(account));
      transactions.sort((a, b) -> b.timestamp.compareTo(a.timestamp));
      return new TransactionHistoryGui(transactions, account);
    })
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
