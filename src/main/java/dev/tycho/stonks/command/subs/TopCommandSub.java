package dev.tycho.stonks.command.subs;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TopCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    sendMessage(player, "Fetching top companies, please wait one moment...");
    Stonks.newChain()
        .async(() -> {
            List<Company> companies = new ArrayList<>(Repo.getInstance().companies().getAll());
            companies.sort((c1, c2) -> (int) (c2.getTotalValue() - c1.getTotalValue()));
            for (int i = 0; i < Math.min(10, companies.size()); i++) {
              Company company = companies.get(i);
              sendMessage(player, "#" + (i + 1) + " - " + ChatColor.YELLOW + company.name + ChatColor.GREEN + ", " + ChatColor.DARK_GREEN + "$" + Util.commify(company.getTotalValue()));
            }
        })
        .execute();
  }
}
