package dev.tycho.stonks.command.subs.holding;

import com.earth2me.essentials.Essentials;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.gui.AccountSelectorGui;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreateHoldingCommandSub extends CommandSub {
  private final Essentials essentials;
  public CreateHoldingCommandSub(Essentials essentials) {
    this.essentials = essentials;
  }


  private static final List<String> RATIOS = Arrays.asList(
      "0.5",
      "1",
      "1.5",
      "3");

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return matchPlayerName(args[1]);
    } else if (args.length == 3) {
      return copyPartialMatches(args[2], RATIOS);
    }
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 3) {
      sendMessage(player, "Correct user: " + ChatColor.YELLOW + "/" + alias + " createholding <player> <share>");
      return;
    }

    double share = Double.parseDouble(args[2]);
    Collection<Company> companies = Repo.getInstance().companiesWhereManager(player);
    new CompanySelectorGui.Builder()
        .companies(companies)
        .title("Select a company")
        .companySelected((company ->
            new AccountSelectorGui.Builder()
                .company(company)
                .title("Select an account")
                .accountSelected(l -> createHolding(player, l, args[1], share))
                .open(player)))
        .open(player);
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

    Member member =  company.getMember(player);
    HoldingsAccount holdingsAccount = (HoldingsAccount)account;
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
    if (!company.hasMember(newHoldingOwner)) {
      sendMessage(player, newHoldingOwner.getDisplayName() +  " isn't a member of the selected company");
      return;
    }

    if (holdingsAccount.getPlayerHolding(newHoldingOwner.getUniqueId()) != null) {
      sendMessage(player, newHoldingOwner.getDisplayName() +  " already has a holding in this holding account!");
      return;
    }
    //We can make a holding
    Repo.getInstance().createHolding(newHoldingOwner.getUniqueId(), holdingsAccount, share);
    sendMessage(player, "Holding successfully created!");
  }
}
