package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ServiceSelectorGui;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.service.Service;
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
      sendMessage(player, "Correct usage:" + ChatColor.YELLOW + "/" + alias + " setservicemax <max_subs (0=unlimited)>");
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Correct usage:" + ChatColor.YELLOW + "/" + alias + " setservicemax <max_subs (0=unlimited)>");
      return;
    }
    int maxSubs = Integer.parseInt(args[1]);

    new CompanySelectorGui.Builder()
        .companies(Repo.getInstance().companiesWhereManager(player))
        .title("Select a company")
        .companySelected(company -> new ServiceSelectorGui.Builder()
            .company(company)
            .title("Select a service")
            .serviceSelected(
                service -> changeServiceMaxSubs(player, maxSubs, company, service)
            ).open(player))
        .open(player);
  }

  private void changeServiceMaxSubs(Player player, int maxSubs, Company company, Service service) {
    Member member = company.getMember(player);

    if (member == null || !member.hasManagamentPermission()) {
      sendMessage(player, "You don't have management perms for this company");
      return;
    }

    if (maxSubs > 0 && maxSubs < service.subscriptions.size()) {
      sendMessage(player, "You can't set the max subscriptions lower than the current number of subscriptions");
      return;
    }

    Repo.getInstance().modifyService(service, service.name, service.duration, service.cost, maxSubs);
      sendMessage(player, "Max subs updated to " + maxSubs);
  }
}
