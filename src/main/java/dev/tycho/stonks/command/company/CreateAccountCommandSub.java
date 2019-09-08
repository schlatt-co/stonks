package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.AccountTypeSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateAccountCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " createaccount <account name>");
      return;
    }
    String accountName = concatArgs(1, args);

    new AccountTypeSelectorGui.Builder()
        .title("Select an account type")
        .typeSelected(type -> {
              List<Company> list;
              //Get all the accounts the player is a manager of
              list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao()
                  .getAllCompaniesWhereManager(player,
                      DatabaseHelper.getInstance().getDatabaseManager().getMemberDao().queryBuilder());
              new CompanySelectorGui.Builder()
                  .title("Select a company")
                  .companies(list)
                  .companySelected(company -> new ConfirmationGui.Builder()
                      .title("Accept creation fee?")
                      .onChoiceMade(b -> {
                        if (b) switch (type) {
                          case HoldingsAccount:
                            DatabaseHelper.getInstance().createCompanyAccount(player, company.getName(), accountName, true);
                            break;
                          case CompanyAccount:
                            DatabaseHelper.getInstance().createCompanyAccount(player, company.getName(), accountName, false);
                            break;
                        }
                      })
                      .open(player))
                  .open(player);
            }
        ).open(player);
  }
}
