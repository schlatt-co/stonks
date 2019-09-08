package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VerifyCommandSub extends CommandSub {

  public VerifyCommandSub() {
    super("trevor.mod");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    new CompanySelectorGui.Builder()
        .title("Select company to verify")
        .companies(DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao().getAllCompanies())
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Verify " + company.getName() + "?")
            .onChoiceMade(c -> {
              if (c) {
                DatabaseHelper.getInstance().changeVerification(player, company.getName(), true);
              }
            })
            .open(player))
        .open(player);
  }
}
