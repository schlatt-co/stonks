package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Company;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateServiceCommandSub extends CommandSub {
  private static final List<String> AMOUNTS = Arrays.asList(
      "1",
      "10",
      "1000",
      "10000");
  private static final List<String> DURATIONS = Arrays.asList(
      "1",
      "2",
      "7",
      "14");
  private static final List<String> MAX_SUBSCRIBERS = Arrays.asList(
      "0",
      "1",
      "5",
      "10",
      "20");


  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    List<String> completions = new ArrayList<>();
    if (args.length == 2) {
      StringUtil.copyPartialMatches(args[1], DURATIONS, completions);
    } else if (args.length == 3) {
      StringUtil.copyPartialMatches(args[2], AMOUNTS, completions);
    } else if (args.length == 4) {
      StringUtil.copyPartialMatches(args[3], MAX_SUBSCRIBERS, completions);
    }
    return completions;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 5) {
      sendMessage(player, ChatColor.RED + "Correct usage: /" + alias + " createservice <duration (days)> <cost> <max_subs (0=unlimited)> <name>");
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Please enter an integer for duration");
      return;
    }
    if (!validateDouble(args[2])) {
      sendMessage(player, "Please enter a number for cost");
      return;
    }
    if (!StringUtils.isNumeric(args[3])) {
      sendMessage(player, "Please enter an integer for max subs");
      return;
    }
    double duration = Double.parseDouble(args[1]);
    double cost = Double.parseDouble(args[2]);
    int maxSubs = Integer.parseInt(args[3]);
    String name = concatArgs(4, args);
    List<Company> list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao()
        .getAllCompaniesWhereManager(player, DatabaseHelper.getInstance().getDatabaseManager().getMemberDao().queryBuilder());
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company")
        .companySelected(company -> {
          new AccountSelectorGui.Builder()
              .title("Select the account profits go to")
              .company(company)
              .accountSelected(
                  accountLink -> DatabaseHelper.getInstance().createService(player, duration, cost, maxSubs, name, company, accountLink)
              ).open(player);
        })
        .open(player);
  }
}
