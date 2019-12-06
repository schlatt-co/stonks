package dev.tycho.stonks.command.subs.holding;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveHoldingCommandSub extends ModularCommandSub {


  public RemoveHoldingCommandSub() {
    super(new AccountValidator("account"), new StringValidator("player_name"));
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 3) {
      return matchPlayerName(args[2]);
    }
    return null;
  }




  @Override
  public void execute(Player player) {
    removeHolding(player, getArgument("account"), getArgument("player_name"));
  }

  private void removeHolding(Player player, Account account, String playerName) {
    if (account == null) {
      sendMessage(player, "Invalid account id!");
      return;
    }
    //We have a valid account
    //First make sure the account is a holdings account
    if (!(account instanceof HoldingsAccount)) {
      sendMessage(player, "That is not a holding account!");
      return;
    }
    HoldingsAccount holdingsAccount = (HoldingsAccount) account;
    Company company = Repo.getInstance().companies().get(account.companyPk);
    if (company == null) {
      sendMessage(player, "Could not find company for account");
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

    //Try and find the UUID of that player
    Player op = playerFromName(playerName);
    if (op == null) {
      sendMessage(player, "That player has never played on the server!");
      return;
    }
    Holding playerHolding = holdingsAccount.getPlayerHolding(op.getUniqueId());
    if (playerHolding == null) {
      sendMessage(player, "There is no holding for this player!");
      return;
    }
    //That player has a holding
    //If their balance is lower than 1 we can remove it
    //This isn't == 0 because of possible floating point errors
    if (playerHolding.balance > 1) {
      sendMessage(player, "That account is worth more than $1! Please get the player to withdraw the money from it!");
      return;
    }
    if (holdingsAccount.holdings.size() < 2) {
      sendMessage(player, "There needs to be at least one holding per holding account!");
      return;
    }
    //We can delete the holding
    if (Repo.getInstance().deleteHolding(playerHolding)) {
      sendMessage(player, "Holding removed successfully!");
    } else {
      sendMessage(player, "Error deleting holding");
    }

  }
}
