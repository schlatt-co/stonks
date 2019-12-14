package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.gui.TransactionHistoryGui;
import dev.tycho.stonks.model.core.Account;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
    new TransactionHistoryGui(account).show(player);
  }
}
