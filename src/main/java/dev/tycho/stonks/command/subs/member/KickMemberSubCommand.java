package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.api.event.CompanyKickEvent;
import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.managers.PlayerData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KickMemberSubCommand extends ModularSubCommand {

  public KickMemberSubCommand() {
    super(new StringValidator("player_name"), new CompanyValidator("company"));
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    kickMember(player, company, getArgument("player_name"));
  }

  private void kickMember(Player player, Company company, String playerName) {
    Player playerToKick = playerFromName(playerName);
    if (playerToKick == null) {
      sendMessage(player, "That player has never played on this server before!");
      return;
    }

    Member memberToKick = company.getMember(playerToKick);
    if (memberToKick == null) {
      sendMessage(player, "That player isn't part of that company!");
      return;
    }

    Member sender = company.getMember(player);
    //If the the member is not kicking themselves and doesnt have management permission
    if (sender == null || (!memberToKick.playerUUID.equals(sender.playerUUID) && !sender.hasManagamentPermission())) {
      sendMessage(player, "You don't have permission to preform that action.");
      return;
    }

    if (memberToKick.role == Role.CEO) {
      sendMessage(player, "You cannot kick the CEO!");
      return;
    }

    boolean hasHoldings = false;
    for (Account account : company.accounts) {
      ReturningAccountVisitor<Boolean> visitor = new ReturningAccountVisitor<>() {
        @Override
        public void visit(CompanyAccount a) {
          val = false;
        }

        @Override
        public void visit(HoldingsAccount a) {
          val = a.getPlayerHolding(playerToKick.getUniqueId()) != null;
        }
      };
      account.accept(visitor);
      hasHoldings = visitor.getRecentVal();
      if (hasHoldings) break;
    }

    if (hasHoldings) {
      sendMessage(player, "That player has holdings! Please delete them before kicking them!");
      return;
    }

    //We can kick the player
    if (Repo.getInstance().deleteMember(memberToKick)) {
      Bukkit.getPluginManager().callEvent(new CompanyKickEvent(company, playerToKick));
      PlayerData.getInstance().getSelectedCompanyChat().remove(Bukkit.getOfflinePlayer(memberToKick.playerUUID).getPlayer());
      sendMessage(player, "Player kicked successfully!");
      //Send a different message based on if a player was fired or left
      if (player.getUniqueId().equals(playerToKick.getUniqueId())) {
        Repo.getInstance().sendMessageToAllOnlineManagers(company,
            player.getDisplayName() + " just resigned from " + ChatColor.GOLD + company.name);
      } else {
        Repo.getInstance().sendMessageToAllOnlineManagers(company,
            player.getDisplayName() + " just fired " + playerName + " from " + ChatColor.GOLD + company.name);
      }

    } else {
      sendMessage(player, "Error kicking player");
    }
  }
}
