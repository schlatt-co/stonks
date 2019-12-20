package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CurrencyValidator;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.core.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class WithdrawCommandSub extends ModularCommandSub {

  private static final List<String> AMOUNTS = Arrays.asList(
      "1",
      "10",
      "1000",
      "10000");

  public WithdrawCommandSub() {
    super(new CurrencyValidator("amount"), ArgumentValidator.optional(new AccountValidator("account_id")));
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
      withdrawFromAccount(player, account, amount);
      return;
    }


    new CompanySelectorGui.Builder()
        .companies(Repo.getInstance().companiesWithWithdrawableAccount(player))
        .title("Select a company to withdraw from")
        .companySelected((company ->
            new AccountSelectorGui.Builder()
                .company(company)
                .title("Select an account to withdraw from")
                .accountSelected(acc -> withdrawFromAccount(player, acc, amount))
                .show(player)))
        .show(player);
  }

  protected void withdrawFromAccount(Player player, Account account, double amount) {
    //We have a valid account
    //First check they are a member of the company
    Company company = Repo.getInstance().companies().get(account.companyPk);
    Member member = company.getMember(player);
    if (member == null) {
      sendMessage(player, "You are not a member of the company the account is in!");
      return;
    }
    if (amount < 0) {
      sendMessage(player, "You cannot withdraw a negative number");
      return;
    }
    IAccountVisitor visitor = new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        //With a company account we need to verify they have withdraw permission
        if (!member.hasManagamentPermission()) {
          sendMessage(player, "You have insufficient permissions to withdraw money from this account!");
          return;
        }
        if (a.getTotalBalance() < amount) {
          sendMessage(player, "That account doesn't have enough funds to complete this transaction!");
          return;
        }

        Repo.getInstance().withdrawFromAccount(player.getUniqueId(), a, amount);
        Stonks.economy.depositPlayer(player, amount);
        sendMessage(player, "Money withdrawn successfully!");
      }

      @Override
      public void visit(HoldingsAccount a) {
        //Check to see if they own a holding in this holdingsaccount
        Holding h = a.getPlayerHolding(player.getUniqueId());
        if (h == null) {
          sendMessage(player, "You do not have a holding in this account!");
          return;
        }

        if (h.balance < amount) {
          sendMessage(player, "That holding doesn't have enough funds to complete this transaction!");
          return;
        }

        Repo.getInstance().withdrawFromHolding(player.getUniqueId(), h, amount);
        Stonks.economy.depositPlayer(player, amount);
        sendMessage(player, "Money withdrawn successfully!");
      }
    };
    account.accept(visitor);
  }
}
