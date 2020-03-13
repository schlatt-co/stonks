package dev.tycho.stonks.command.stonks.subs.holding;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.OptionListAutocompleter;
import dev.tycho.stonks.command.base.autocompleters.PlayerNameAutocompleter;
import dev.tycho.stonks.command.base.validators.DoubleValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CreateHoldingCommandSub extends ModularCommandSub {

  public CreateHoldingCommandSub() {
    super(new StringValidator("player_name"), new DoubleValidator("share"));
    addAutocompleter("player_name", new PlayerNameAutocompleter());
    addAutocompleter("share", new OptionListAutocompleter("1", "1.5", "2", "10"));
  }

  @Override
  public void execute(Player player) {
    double share = getArgument("share");
    String otherPlayer = getArgument("player_name");

    Collection<Company> companies = Repo.getInstance().companiesWhereManager(player);
    new CompanySelectorGui.Builder()
        .companies(companies)
        .title("Select a company")
        .companySelected((company ->
            new AccountSelectorGui.Builder()
                .company(company)
                .title("Select an account")
                .accountSelected(l -> createHolding(player, l, otherPlayer, share))
                .show(player)))
        .show(player);
  }

  private void createHolding(Player player, Account account, String playerName, double share) {
    if (share <= 0) {
      sendMessage(player, "Holding share must be greater than 0");
      return;
    }
    if (account == null) {
      sendMessage(player, "Invalid account");
      return;
    }

    //We have a valid account
    Company company = Repo.getInstance().companies().get(account.companyPk);
    if (company == null) {
      sendMessage(player, "Error finding company for account");
      return;
    }

    //First make sure the account is a holdings account
    if (!(account instanceof HoldingsAccount)) {
      sendMessage(player, "That is not a Holdings Account!");
      return;
    }

    Member member = company.getMember(player);
    HoldingsAccount holdingsAccount = (HoldingsAccount) account;
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

    //Find player UUID
    Player newHoldingOwner = playerFromName(playerName);
    if (newHoldingOwner == null) {
      sendMessage(player, playerName + " has never played on the server!");
      return;
    }
    //Check they are a member of that company
    if (!company.isMember(newHoldingOwner)) {
      sendMessage(player, newHoldingOwner.getDisplayName() + " isn't a member of the selected company");
      return;
    }

    if (holdingsAccount.getPlayerHolding(newHoldingOwner.getUniqueId()) != null) {
      sendMessage(player, newHoldingOwner.getDisplayName() + " already has a holding in this holding account!");
      return;
    }
    //We can make a holding
    Repo.getInstance().createHolding(newHoldingOwner.getUniqueId(), holdingsAccount, share);
    sendMessage(player, "Holding successfully created!");
    if (!player.getUniqueId().equals(newHoldingOwner.getUniqueId())) {
      Repo.getInstance().sendMessageToPlayer(player.getUniqueId(), "A holding was created for you in the account " + account.name
          + " (" + ChatColor.GOLD + company.name + ChatColor.WHITE + ")");
    }
  }
}
