package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.command.base.Argument;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.CurrencyArgument;
import dev.tycho.stonks.command.base.StringArgument;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
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

import static dev.tycho.stonks.model.core.Role.CEO;

public class PayCommandSub extends ModularCommandSub {

  private static final List<String> AMOUNTS = Arrays.asList(
      "1",
      "10",
      "1000",
      "10000");

  protected PayCommandSub() {
    super(new CurrencyArgument("amount"), Argument.optional(new StringArgument("Message", 200)));
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
                  .accountSelected(account -> Repo.getInstance().payAccount(player.getUniqueId(), message, account, amount));
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
              if (p != null) ceoName = p.getName();
            }
          }
          info.add(ChatColor.GOLD + ceoName);
          if (!company.verified) {
            new ConfirmationGui.Builder()
                .title(company.name + " is unverified")
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
