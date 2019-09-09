package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.DatabaseHelper;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static dev.tycho.stonks.model.core.Role.CEO;

public class PayCommandSub extends CommandSub {

  private static final List<String> AMOUNTS = Arrays.asList(
      "1",
      "10",
      "1000",
      "10000");

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return copyPartialMatches(args[1], AMOUNTS);
    }
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " pay <amount> [<message>]");
      return;
    }

    if (!Pattern.matches("([0-9]*)\\.?([0-9]*)?", args[1])) {
      sendMessage(player, "Invalid amount!");
      return;
    }
    double amount = Double.parseDouble(args[1]);

    String message = (args.length > 2) ? concatArgs(2, args) : "";
    if (message.length() > 200) {
      sendMessage(player, "Your message cannot be longer than 200 characters!");
      return;
    }

    List<Company> list = DatabaseHelper.getInstance().getDatabaseManager().getCompanyDao().getAllCompanies();
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company to pay")
        .companySelected((company -> {
          //Cache the next screen
          AccountSelectorGui.Builder accountSelectorScreen =
              new AccountSelectorGui.Builder()
                  .company(company)
                  .title("Select which account to pay")
                  .accountSelected(l -> DatabaseHelper.getInstance().payAccount(player, l.getId(), message, amount));
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
          for (Member m : company.getMembers()) {
            if (m.getRole().equals(CEO)) {
              OfflinePlayer p = Bukkit.getOfflinePlayer(m.getUuid());
              if (p != null) ceoName = p.getName();
            }
          }
          info.add(ChatColor.GOLD + ceoName);
          if (!company.isVerified()) {
            new ConfirmationGui.Builder()
                .title(company.getName() + " is unverified")
                .info(info)
                .onChoiceMade(
                    c -> {
                      if (c) accountSelectorScreen.open(player);
                    }
                ).open(player);
          } else {
            accountSelectorScreen.open(player);
          }
        }))
        .open(player);
  }
}
