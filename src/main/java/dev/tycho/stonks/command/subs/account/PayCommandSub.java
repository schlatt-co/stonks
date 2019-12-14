package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CurrencyValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.tycho.stonks.model.core.Role.CEO;

public class PayCommandSub extends ModularCommandSub {

  private static final List<String> AMOUNTS = Arrays.asList(
      "1",
      "10",
      "1000",
      "10000");

  public PayCommandSub() {
    super(new CurrencyValidator("amount"), ArgumentValidator.optionalAndConcatIfLast(new StringValidator("message", 200)));
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return copyPartialMatches(args[1], AMOUNTS);
    }
    return null;
  }

  @Override
  public void execute(Player player) {
    double amount = getArgument("amount");
    String message = getArgument("message");

    List<Company> list = Repo.getInstance().companies().getAll();
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company to pay")
        .companySelected((company -> {
          //Cache the next screen
          AccountSelectorGui.Builder accountSelectorScreen =
              new AccountSelectorGui.Builder()
                  .company(company)
                  .title("Select which account to pay")
                  .accountSelected(account -> payAccount(player, account, message, amount));
          List<String> info = new ArrayList<>();
          info.add("You are trying to pay an unverified company!");
          info.add("Unverified companies might be pretending to be ");
          info.add("someone else's company");
          info.add("Make sure you are paying the correct company");
          info.add("(e.g. by checking the CEO is who you expect)");
          info.add("To get a company verified, ask a moderator.");
          info.add("");
          info.add(ChatColor.GOLD + "The CEO of this company is ");
          String ceoName = "[error lol]";
          for (Member m : company.members) {
            if (m.role.equals(CEO)) {
              OfflinePlayer p = Bukkit.getOfflinePlayer(m.playerUUID);
              ceoName = p.getName();
            }
          }
          info.add(ChatColor.GOLD + ceoName);
          if (!company.verified) {
            new ConfirmationGui.Builder()
                .title(company.name + " is unverified")
                .info(info)
                .yes(() -> accountSelectorScreen.show(player)
                ).show(player);
          } else {
            accountSelectorScreen.show(player);
          }
        }))
        .show(player);
  }

  private void payAccount(Player sender, Account account, String message, double amount) {
    if (amount < 0) {
      sendMessage(sender, "You cannot pay a negative number");
      return;
    }

    if (!Stonks.economy.withdrawPlayer(sender, amount).transactionSuccess()) {
      sendMessage(sender, "Insufficient funds!");
      return;
    }
    Repo.getInstance().payAccount(sender.getUniqueId(), message, account, amount);
    Company company = Repo.getInstance().companies().get(account.companyPk);
    //Tell the user we paid the account
    sendMessage(sender, "Paid " + ChatColor.YELLOW + company.name + " (" + account.name + ")" + ChatColor.YELLOW + " $" + Util.commify(amount) + ChatColor.GREEN + "!");

    //Send a message to all managers in the company that are online that the company got paid
    for (Member member : company.members) {
      if (member.hasManagamentPermission()) {
        Player u = Stonks.essentials.getUser(member.playerUUID).getBase();
        if (!u.getName().equalsIgnoreCase(sender.getName()) && u.isOnline()) {
          sendMessage(u, sender.getDisplayName() + ChatColor.GREEN + " paid " + ChatColor.YELLOW + " " + company.name + " (" + account.name + ") $" + Util.commify(amount));
        }
      }
    }
  }
}
