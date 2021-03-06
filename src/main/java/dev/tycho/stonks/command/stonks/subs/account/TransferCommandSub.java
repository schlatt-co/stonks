package dev.tycho.stonks.command.stonks.subs.account;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CurrencyAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CurrencyValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TransferCommandSub extends ModularCommandSub {

  public TransferCommandSub() {
    super(new CurrencyValidator("amount"), ArgumentValidator.optionalAndConcatIfLast(new StringValidator("message", 200)));
    addAutocompleter("amount", new CurrencyAutocompleter());
  }

  @Override
  public void execute(Player player) {
    double amount = getArgument("amount");
    String msg = getArgument("message");
    new CompanySelectorGui.Builder()
        .companies(Repo.getInstance().companiesWithWithdrawableAccount(player))
        .title("Select company to transfer from")
        .companySelected((company -> {
          //Cache the next screen
          new AccountSelectorGui.Builder()
              .company(company)
              .title("Select account to transfer from")
              .accountSelected(transferFrom -> Bukkit.getScheduler().runTaskLater(Stonks.getInstance(), () -> new CompanySelectorGui.Builder()
                  .companies(Repo.getInstance().companies().getAll())
                  .title("Select a company to transfer to")
                  .companySelected((companyTo -> {
                    //Cache the next screen
                    new AccountSelectorGui.Builder()
                        .company(companyTo)
                        .title("Select which account to transfer to")
                        .accountSelected(transferTo -> {
                          String message = "Transferred money to " + companyTo.name + "#" + transferTo.pk + "(" + transferTo.name + ")";
                          if (msg != null) message += " [message: \"" + msg + "\"]";
                          payCompany(player, transferFrom, transferTo, companyTo, message, amount);
                        })
                        .show(player);
                  }))
                  .show(player), 1))
              .show(player);
        }))
        .show(player);
  }

  private void payCompany(Player sender, Account transferFrom, Account transferTo, Company companyTo, String message, double amount) {
    if (amount < 0) {
      sendMessage(sender, "You cannot pay a negative number");
      return;
    }

    if (transferFrom.getTotalBalance() < amount) {
      sendMessage(sender, "Insufficient funds!");
      return;
    }

    IAccountVisitor visitor = new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        //With a company account we need to verify they have withdraw permission
        if (!Repo.getInstance().companies().get(transferFrom.companyPk).getMember(sender).hasManagamentPermission()) {
          sendMessage(sender, "You have insufficient permissions to withdraw money from this account!");
          return;
        }
        if (a.getTotalBalance() < amount) {
          sendMessage(sender, "That account doesn't have enough funds to complete this transaction!");
          return;
        }

        Repo.getInstance().withdrawFromAccount(sender.getUniqueId(), a, amount, message);
      }

      @Override
      public void visit(HoldingsAccount a) {
        //Check to see if they own a holding in this holdingsaccount
        Holding h = a.getPlayerHolding(sender.getUniqueId());
        if (h == null) {
          sendMessage(sender, "You do not have a holding in this account!");
          return;
        }

        if (h.balance < amount) {
          sendMessage(sender, "That holding doesn't have enough funds to complete this transaction!");
          return;
        }

        Repo.getInstance().withdrawFromHolding(sender.getUniqueId(), h.pk, amount, message);
      }
    };
    transferFrom.accept(visitor);

    Repo.getInstance().payAccount(sender.getUniqueId(), message, transferTo.pk, amount);

    //Tell the user we paid the account
    sendMessage(sender, "Paid " + ChatColor.YELLOW + companyTo.name + " (" + transferTo.name + ")" + ChatColor.YELLOW + " $" + Util.commify(amount) + ChatColor.GREEN + "!");

    //Send a message to all managers in the company that are online that the company got paid
    Repo.getInstance().sendMessageToAllOnlineManagers(
        companyTo, sender.getDisplayName() + ChatColor.GREEN + " paid " + ChatColor.YELLOW + " " + companyTo.name + " (" + transferTo.name + ") $" + Util.commify(amount));
  }
}
