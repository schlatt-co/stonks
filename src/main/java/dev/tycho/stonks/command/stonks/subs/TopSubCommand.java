package dev.tycho.stonks.command.stonks.subs;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TopSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    sendMessage(player, "Fetching top companies, please wait one moment...");
    //Run the command asynchronously
    new Thread(() -> {
      List<Company> companies = new ArrayList<>(Repo.getInstance().companies().getAll());
      companies.sort((c1, c2) -> (int) (c2.getTotalValue() - c1.getTotalValue()));
      for (int i = 0; i < Math.min(10, companies.size()); i++) {
        Company company = companies.get(i);
        sendMessage(player, "#" + (i + 1) + " - " + ChatColor.YELLOW + company.name + ChatColor.GREEN + ", " + ChatColor.DARK_GREEN + "$" + Util.commify(company.getTotalValue()));
      }
    }).start();
  }
}
