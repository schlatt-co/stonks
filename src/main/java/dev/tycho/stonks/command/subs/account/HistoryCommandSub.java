package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.TransactionHistoryGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.AccountLink;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class HistoryCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " history [-v] <account id>");
      return;
    }
    if (args.length == 2) {
      AccountLink link;
      try {
        link = DatabaseHelper.getInstance().getDatabaseManager().getAccountLinkDao().queryForId(Integer.parseInt(args[1]));
      } catch (SQLException e) {
        e.printStackTrace();
        player.sendMessage(ChatColor.RED + "SQL ERROR");
        return;
      }

      if (link == null) {
        player.sendMessage(ChatColor.RED + "Account not found");
        return;
      }
      new TransactionHistoryGui.Builder()
          .accountLink(link)
          .title("Transaction History")
          .open(player);
      return;
    }
    if (args.length > 3) {
      if (args[1].equals("-v")) {
        DatabaseHelper.getInstance().transactionHistoryPagination(player, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
      }
    } else {
      List<Company> list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao()
          .getAllCompaniesWhereManager(player, DatabaseHelper.getInstance().getDatabaseManager().getMemberDao().queryBuilder());
      new CompanySelectorGui.Builder()
          .companies(list)
          .title("Select a company")
          .companySelected((company ->
              new AccountSelectorGui.Builder()
                  .company(company)
                  .title("Select an account")
                  .accountSelected(l -> new TransactionHistoryGui.Builder()
                      .accountLink(l)
                      .title("Transaction History")
                      .open(player))
                  .open(player)))
          .open(player);
    }
  }
}
