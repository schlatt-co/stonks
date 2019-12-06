package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.CurrencyValidator;
import dev.tycho.stonks.command.base.validators.DoubleValidator;
import dev.tycho.stonks.command.base.validators.IntegerValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.service.Service;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateServiceCommandSub extends ModularCommandSub {

  public CreateServiceCommandSub() {
    super(new DoubleValidator("duration"), new CurrencyValidator("cost"), new IntegerValidator("max_subs"), new StringValidator("name", 40));
  }

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
  public void execute(Player player) {
    double duration = getArgument("duration");
    double cost = getArgument("cost");
    int maxSubs = getArgument("max_subs");
    String name = getArgument("name");

    new CompanySelectorGui.Builder()
        .companies(Repo.getInstance().companiesWhereManager(player))
        .title("Select a company")
        .companySelected(company -> {
          new AccountSelectorGui.Builder()
              .title("Select the account profits go to")
              .company(company)
              .accountSelected(
                  account -> createService(player, duration, cost, maxSubs, name, company, account)
              ).open(player);
        })
        .open(player);
  }

  void createService(Player player, double duration, double cost, int maxSubs, String name, Company company, Account account) {
    //Check for the same name
    for (Account a : company.accounts) {
      for (Service service : a.services) {
        if (service.name.equals(name)) {
          sendMessage(player, "A service with the same name already exists for this company");
          return;
        }
      }
    }

    if (duration <= 0.5) {
      sendMessage(player, "Service duration must be greater than 0.5 (12 hours)");
      return;
    }

    if (cost < 0) {
      sendMessage(player, "A service cannot have a negative cost. Nice try");
      return;
    }

    if (!name.matches("[0-9a-zA-Z\\s&+]{2,40}")) {
      sendMessage(player, "Invalid name. Please try again. You may have used special characters or it is too long");
      return;
    }

    if (StringUtils.isNumeric(name)) {
      sendMessage(player, "A company name cannot be a number!");
      return;
    }

    //Only verified companies can create services
    if (!company.verified) {
      sendMessage(player, "Your company must be verified before you can create a service. Ask a moderator to consider verifying your company.");
      return;
    }

    Repo.getInstance().createService(name, duration, cost, maxSubs, account);
    sendMessage(player, "Service created!");
  }
}
