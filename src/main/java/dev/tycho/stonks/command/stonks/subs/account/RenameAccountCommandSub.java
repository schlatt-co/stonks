package dev.tycho.stonks.command.stonks.subs.account;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.OptionListAutocompleter;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.entity.Player;

import java.util.Collection;

public class RenameAccountCommandSub extends ModularCommandSub {
  public RenameAccountCommandSub() {
    super(new StringValidator("new_name"));
    addAutocompleter("new_name", new OptionListAutocompleter("sales", "shop", "profits"));
  }


  @Override
  public void execute(Player player) {
    String newName = getArgument("new_name");

    Collection<Company> list = Repo.getInstance().companiesWhereManager(player);
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company")
        .companySelected(company -> new AccountSelectorGui.Builder()
            .company(company)
            .title("Select which account to rename")
            .accountSelected(account -> renameAccount(player, company, account, newName))
            .show(player))
        .show(player);
  }

  private static void renameAccount(Player player, Company company, Account account, String newName) {
    if (company == null) {
      sendMessage(player, "Invalid company!");
      return;
    }

    Member member = company.getMember(player);
    //Is the player a member of that company
    if (member == null) {
      sendMessage(player, "You are not a member of that company!");
      return;
    }

    //Does the player have permission to create a holding in that account?
    if (!member.hasManagamentPermission()) {
      sendMessage(player, "You do not have permission to create a holding account! Ask to be promoted.");
      return;
    }


    // Check the account name isn't already in use
    for (Account a : company.accounts) {
      if (a.name.equals(newName)) {
        sendMessage(player, "Account name already exists in company!");
        return;
      }
    }

    // We can now rename
    account = Repo.getInstance().renameAccount(account.pk, newName);
    sendMessage(player, "Account renamed to " + account.name + "!");
  }
}
