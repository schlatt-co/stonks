package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateServiceCommandSub extends CommandSub {
  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length > 4) {
      double duration = Double.parseDouble(args[1]);
      double cost = Double.parseDouble(args[2]);
      int maxSubs = Integer.parseInt(args[3]);
      String name = concatArgs(4, args);
      List<Company> list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao()
          .getAllCompaniesWhereManager(player, DatabaseHelper.getInstance().getDatabaseManager().getMemberDao().queryBuilder());
      new CompanySelectorGui.Builder()
          .companies(list)
          .title("Select a company")
          .companySelected(company -> {
            new AccountSelectorGui.Builder()
                .title("Select the account profits go to")
                .company(company)
                .accountSelected(
                    accountLink -> DatabaseHelper.getInstance().createService(player, duration, cost, maxSubs, name, company, accountLink)
                ).open(player);
          })
          .open(player);
    } else {

      player.sendMessage(ChatColor.RED + "Correct usage: /" + alias + " createservice <duration (days)> <cost> <max_subs (0=unlimited)> <name>");
    }
  }
}
