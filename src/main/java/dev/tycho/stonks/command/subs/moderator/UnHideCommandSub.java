package dev.tycho.stonks.command.subs.moderator;

import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnHideCommandSub extends CommandSub {

  public UnHideCommandSub() {
    super("trevor.mod");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    List<Company> companies = new ArrayList<>();
    QueryBuilder<Company, UUID> queryBuilder = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao().queryBuilder();
    queryBuilder.orderBy("name", true);
    try {
      queryBuilder.where().eq("hidden", true);
      companies = queryBuilder.query();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    new CompanySelectorGui.Builder()
        .title("Select company to unhide")
        .companies(companies)
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Unhide " + company.getName() + "?")
            .onChoiceMade(c -> {
              if (c) {
                DatabaseHelper.getInstance().changeHidden(player, company.getName(), false);
              }
            })
            .open(player))
        .open(player);
  }
}
