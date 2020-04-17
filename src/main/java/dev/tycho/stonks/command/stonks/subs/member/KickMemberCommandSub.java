package dev.tycho.stonks.command.stonks.subs.member;

import dev.tycho.stonks.api.event.CompanyKickEvent;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.managers.PlayerData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KickMemberCommandSub extends ModularCommandSub {

  public KickMemberCommandSub() {
    super(new StringValidator("player_uuid"), new CompanyValidator("company"));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    kickMember(player, company, getArgument("player_uuid"));
  }

  private void kickMember(Player player, Company company, String playerUUID) {
    UUID uuid = UUID.fromString((playerUUID));

    Member memberToKick = company.getMember(uuid);
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

    //Check to see if the player has any money in holdings
    boolean hasHoldings = false;
    for (Account account : company.accounts) {
      ReturningAccountVisitor<Boolean> visitor = new ReturningAccountVisitor<>() {
        @Override
        public void visit(CompanyAccount a) {
          val = false;
        }

        @Override
        public void visit(HoldingsAccount a) {
          val = a.getPlayerHolding(uuid) != null;
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

      // Try and remove the player from company chats
      Player playerToKick = Bukkit.getOfflinePlayer(uuid).getPlayer();
      if  (playerToKick == null) {
        sendMessage(player, "Player kicked successfully! (they have been inactive)");
        return;
      }
      Bukkit.getPluginManager().callEvent(new CompanyKickEvent(company, playerToKick));
      PlayerData.getInstance().getSelectedCompanyChat().remove(Bukkit.getOfflinePlayer(memberToKick.playerUUID).getPlayer());
      sendMessage(player, "Player kicked successfully!");
      //Send a different message based on if a player was fired or left
      if (player.getUniqueId().equals(playerToKick.getUniqueId())) {
        Repo.getInstance().sendMessageToAllOnlineManagers(company,
            player.getDisplayName() + " just resigned from " + ChatColor.GOLD + company.name);
      } else {
        Repo.getInstance().sendMessageToAllOnlineManagers(company,
            player.getDisplayName() + " just fired " + playerToKick.getDisplayName() + " from " + ChatColor.GOLD + company.name);
      }

    } else {
      sendMessage(player, "Error kicking player");
    }
  }
}
