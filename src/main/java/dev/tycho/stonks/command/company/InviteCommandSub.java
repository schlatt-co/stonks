package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InviteCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return matchPlayerName(args[1]);
    }
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " invite <player>");
      return;
    }
    List<Company> list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao().getAllCompaniesWhereManager(player, DatabaseHelper.getInstance().getDatabaseManager().getMemberDao().queryBuilder());
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company to invite to")
        .companySelected((company -> DatabaseHelper.getInstance().invitePlayerToCompany(player, company.getName(), args[1])))
        .open(player);
  }
}
