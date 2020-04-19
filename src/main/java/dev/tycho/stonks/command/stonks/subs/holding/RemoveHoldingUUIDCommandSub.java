package dev.tycho.stonks.command.stonks.subs.holding;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.logging.Transaction;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class RemoveHoldingUUIDCommandSub extends ModularCommandSub {

  public RemoveHoldingUUIDCommandSub() {
    super(new AccountValidator("account"), new StringValidator("player_uuid"));
  }

  @Override
  public void execute(Player player) {
    removeHolding(player, getArgument("account"), getArgument("player_uuid"));
  }

  private void removeHolding(Player player, Account account, String playerUUID) {
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

    //Make sure we don't remove the last holding in an account
    if (holdingsAccount.holdings.size() < 2) {
      sendMessage(player, "You cannot remove the last holding in a holdings account");
      return;
    }

    //Make UUID
    UUID uuid = UUID.fromString(playerUUID);
    Holding holding = holdingsAccount.getPlayerHolding(uuid);
    if (holding == null) {
      sendMessage(player, "There is no holding for this player!");
      return;
    }

    // If player doesnt own that holding make sure they are can remove it
    if (!holding.playerUUID.equals(player.getUniqueId())) {
      if (company.getMember(player).role != Role.CEO) {
        sendMessage(player, "Only a CEO can remove someone else's holding");
        return;
      }
      //Now find out the last time that player withdrew from the account
      long recentWithdraw = findMostRecentWithdraw(
          Repo.getInstance().transactions().getTransactionsForAccount(holdingsAccount), uuid);
      if (System.currentTimeMillis() - recentWithdraw < 604800000L) {
        //If a withdraw happened less than 7 days ago then the account is still active
        sendMessage(player, "That holding is still active (was withdrawn from less than 7 days ago) so you cannot remove it.");
        return;
      }
      // We can remove the holding

    }


    //If there is money in the holding then prompt to confirm
    if (holding.balance > 1) {
      new ConfirmationGui.Builder().title("That holding has money in it, proceed?")
          .info(Arrays.asList(
              "That holding still has money in it",
              "Deleting it will distribute the money proportionally",
              "amongst the other holding owners",
              "If this is your holding, withdraw your money!",
              ""))
          .yes(() -> {
                // Delete the holding
                if (Repo.getInstance().removeHolding(holding, uuid)) {
                  sendMessage(player, "Holding removed successfully!");
                } else {
                  sendMessage(player, "Error deleting holding");
                }
              }
          )
          .no(() -> sendMessage(player, "Deleting holding cancelled"))
          .show(player);
    }
  }

  private long findMostRecentWithdraw(Collection<Transaction> transactions, UUID playerUUID) {
    long highestMillis = 0;
    // Find the most
    for (Transaction transaction : transactions) {
      if (transaction.payeeUUID.equals(playerUUID) && transaction.amount < 0) {
        highestMillis = Math.max(highestMillis, transaction.timestamp.getTime());
      }
    }
    return highestMillis;
  }
}
