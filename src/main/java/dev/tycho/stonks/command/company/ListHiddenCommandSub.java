package dev.tycho.stonks.command.company;

import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanyListGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListHiddenCommandSub extends CommandSub {

  public ListHiddenCommandSub() {
    super("trevor.mod");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    Stonks.newChain()
        .asyncFirst(() -> {
          List<Company> companies = new ArrayList<>();
          QueryBuilder<Company, UUID> queryBuilder = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao().queryBuilder();
          queryBuilder.orderBy("name", true);
          try {
            queryBuilder.where().eq("hidden", true);
            companies = queryBuilder.query();
          } catch (SQLException e) {
            e.printStackTrace();
          }
          return new CompanyListGui(companies);
        }).sync(gui -> gui.show(player))
        .execute();
  }
}
