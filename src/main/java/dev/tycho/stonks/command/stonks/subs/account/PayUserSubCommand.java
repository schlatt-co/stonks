package dev.tycho.stonks.command.stonks.subs.account;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CurrencyValidator;
import dev.tycho.stonks.command.base.validators.OnlinePlayerValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.core.*;
import org.bukkit.entity.Player;

public class PayUserSubCommand extends ModularSubCommand {

  public PayUserSubCommand() {
    super(new OnlinePlayerValidator("player"), new CurrencyValidator("amount"), ArgumentValidator.optionalAndConcatIfLast(new StringValidator("message", 200)));
  }


  @Override
  public void execute(Player player) {
    Player target = getArgument("player");
    double amount = getArgument("amount");
    String message = getArgument("message");

    new CompanySelectorGui.Builder()
        .companies(Repo.getInstance().companiesWithWithdrawableAccount(player))
        .title("Select a company to withdraw from")
        .companySelected((company ->
            new AccountSelectorGui.Builder()
                .company(company)
                .title("Select an account to withdraw from")
                .accountSelected(acc -> withdrawFromAccount(player, target, acc, amount, message))
                .show(player)))
        .show(player);
  }

  protected void withdrawFromAccount(Player player, Player target, Account account, double amount, String message) {
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

        Repo.getInstance().withdrawFromAccount(player.getUniqueId(), a, amount, message);
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

        Repo.getInstance().withdrawFromHolding(player.getUniqueId(), h, amount, message);
      }
    };
    account.accept(visitor);
    Stonks.economy.depositPlayer(target, amount);
    sendMessage(player, "Paid " + target.getName() + " $" + amount + "!");
    sendMessage(target, "You received $" + amount + " from " + target.getName() + "!");
  }
}
