package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.gui.TransactionHistoryGui;
import dev.tycho.stonks.model.core.Account;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " history <account id>");
      return;
    }
    if (args.length == 2) {
      int id = Integer.parseInt(args[1]);
      Account account = Repo.getInstance().accountWithId(id);
      if (account == null) {
        player.sendMessage(ChatColor.RED + "Account not found");
        return;
      }
      new TransactionHistoryGui.Builder()
          .account(account)
          .title("Transaction History")
          .open(player);
    }
  }
}
