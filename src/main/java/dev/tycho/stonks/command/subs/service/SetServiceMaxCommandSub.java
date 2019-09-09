package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ServiceSelectorGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Company;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetServiceMaxCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2) {
      sendMessage(player, "Correct usage:" + ChatColor.YELLOW +  "/" + alias + " setservicemax <max_subs (0=unlimited)>");
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Correct usage:" + ChatColor.YELLOW +  "/" + alias + " setservicemax <max_subs (0=unlimited)>");
      return;
    }
    int maxSubs = Integer.parseInt(args[1]);
    List<Company> list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao()
        .getAllCompaniesWhereManager(player, DatabaseHelper.getInstance().getDatabaseManager().getMemberDao().queryBuilder());
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company")
        .companySelected(company -> {
          new ServiceSelectorGui.Builder()
              .company(company)
              .title("Select a service")
              .serviceSelected(
                  service -> {
                    DatabaseHelper.getInstance().changeServiceMaxSubs(player, maxSubs, service);
                  }
              ).open(player);
        })
        .open(player);
  }
}
