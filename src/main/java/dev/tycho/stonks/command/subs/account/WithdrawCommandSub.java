package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.command.base.AccountArgument;
import dev.tycho.stonks.command.base.Argument;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.CurrencyArgument;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WithdrawCommandSub extends ModularCommandSub {

  private static final List<String> AMOUNTS = Arrays.asList(
      "1",
      "10",
      "1000",
      "10000");

  protected WithdrawCommandSub(Argument argument, Argument... arguments) {
    super(new CurrencyArgument("amount"), Argument.optional(new AccountArgument("account_id")));
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
    Account account = getArgument("account_id");

    if (account != null) {
        Repo.getInstance().withdrawFromAccount(player.getUniqueId(), account, amount);
        return;
    }
    List<Company> list = new ArrayList<>(Repo.getInstance().companies().getAll());
    //We need a list of all companies with a withdrawable account for this player
    //Remove companies where the player is not a manager and doesn't have an account
    //todo remove this messy logic
    for (int i = list.size() - 1; i >= 0; i--) {
      boolean remove = true;
      Company c = list.get(i);
      Member m = c.getMember(player);
      if (m != null && m.hasManagamentPermission()) {
        //If a manager or ceo
        remove = false;
      }
      //If you are not a manager, or a non-member with a holding then don't remove
      for (Account a : c.accounts) {
        //Is there a holding account for the player
        ReturningAccountVisitor<Boolean> visitor = new ReturningAccountVisitor<>() {
          @Override
          public void visit(CompanyAccount a) {
            val = false;
          }

          @Override
          public void visit(HoldingsAccount a) {
            val = (a.getPlayerHolding(player.getUniqueId()) != null);
          }
        };
        a.accept(visitor);
        if (visitor.getRecentVal()) remove = false;
      }
      if (remove) list.remove(i);
    }

    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company to withdraw from")
        .companySelected((company ->
            new AccountSelectorGui.Builder()
                .company(company)
                .title("Select an account to withdraw from")
                .accountSelected(acc -> Repo.getInstance().withdrawFromAccount(player.getUniqueId(), acc, amount))
                .open(player)))
        .open(player);
  }

}
