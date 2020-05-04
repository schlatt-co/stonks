package dev.tycho.stonks.command.stonks.subs.account;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CurrencyAutocompleter;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CurrencyValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PayAccountCommandSub extends ModularCommandSub {
  protected static final List<String> UNVERIFIED = Arrays.asList(
      "You are trying to pay an unverified company!",
      "Unverified companies might be pretending to be ",
      "someone else's company",
      "Make sure you are paying the correct company",
      "(e.g. by checking the CEO is who you expect)",
      "To get a company verified, ask a moderator.",
      "");

  public PayAccountCommandSub() {
    super(new AccountValidator("account"), new CurrencyValidator("amount"), ArgumentValidator.optionalAndConcatIfLast(new StringValidator("message", 200)));
    addAutocompleter("amount", new CurrencyAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Account account = getArgument("account");
    double amount = getArgument("amount");
    String msg = getArgument("message");

    Company company = Repo.getInstance().companies().get(account.companyPk);
    String message = "Deposit" + company.name + "#" + account.pk
        + ((msg != null) ? " [message: \"" + msg + "\"]" : "");
    if (!company.verified) {
      List<String> info = new ArrayList<>(UNVERIFIED);
      new ConfirmationGui.Builder()
          .title(company.name + " is unverified")
          .info(info)
          .yes(() -> PayCommandSub.payAccount(player, account, message, amount))
          .show(player);
    } else {
      PayCommandSub.payAccount(player, account, message, amount);
    }
  }
}
