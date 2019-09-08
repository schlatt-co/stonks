package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RenameCommandSub extends CommandSub {

  public RenameCommandSub() {
    super("trevor.mod");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " rename <new name>");
      return;
    }

    String newName = concatArgs(1, args);
    new CompanySelectorGui.Builder()
        .title("Select company to rename")
        .companies(DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao().getAllCompanies())
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Rename " + company.getName() + " to" + newName + "?")
            .onChoiceMade(c -> {
              if (c) {
                DatabaseHelper.getInstance().renameCompany(player, company.getName(), newName);
              }
            })
            .open(player))
        .open(player);
  }
}
