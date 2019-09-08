package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LogoCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    List<Company> list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao()
        .getAllCompaniesWhereManager(player, DatabaseHelper.getInstance().getDatabaseManager().getMemberDao().queryBuilder());
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select company logo to change")
        .companySelected((company -> DatabaseHelper.getInstance().setLogo(player, company.getName())))
        .open(player);
  }
}
