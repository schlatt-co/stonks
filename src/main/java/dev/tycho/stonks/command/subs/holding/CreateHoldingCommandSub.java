package dev.tycho.stonks.command.subs.holding;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CreateHoldingCommandSub extends CommandSub {

  private static final List<String> RATIOS = Arrays.asList(
      "0.5",
      "1",
      "1.5",
      "3");

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return matchPlayerName(args[1]);
    } else if (args.length == 3) {
      return copyPartialMatches(args[2], RATIOS);
    }
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 3) {
      sendMessage(player, "Correct user: " + ChatColor.YELLOW + "/" + alias + " createholding <player> <share>");
      return;
    }

    double share = Double.parseDouble(args[2]);
    List<Company> list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao()
        .getAllCompaniesWhereManager(player, DatabaseHelper.getInstance().getDatabaseManager().getMemberDao().queryBuilder());
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company")
        .companySelected((company ->
            new AccountSelectorGui.Builder()
                .company(company)
                .title("Select an account")
                .accountSelected(l -> DatabaseHelper.getInstance().createHolding(player, l.getId(), args[1], share))
                .open(player)))
        .open(player);
  }
}
