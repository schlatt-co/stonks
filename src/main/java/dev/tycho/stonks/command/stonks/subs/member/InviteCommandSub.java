package dev.tycho.stonks.command.stonks.subs.member;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class InviteCommandSub extends ModularCommandSub {


  public InviteCommandSub() {
    super(new StringValidator("player_name"));
  }

  @Override
  public void execute(Player player) {

    Collection<Company> list = Repo.getInstance().companiesWhereManager(player);
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select a company to invite to")
        .companySelected((company -> invitePlayerToCompany(player, company, getArgument("player_name"))))
        .show(player);
  }


  public void invitePlayerToCompany(Player player, Company company, String playerToInvite) {
    Player playerToInviteObject = playerFromName(playerToInvite);
    if (player == null) {
      sendMessage(player, playerToInvite + " has never played on the server before!");
      return;
    }
    //Make sure player has invite permissions
    if (company.getMember(player) == null || !company.getMember(player).hasManagamentPermission()) {
      sendMessage(player, "You don't have permission to do that!");
      return;
    }

    //If the player to invite has already been added then don't invite them again
    if (company.getMember(playerToInviteObject) != null) {
      sendMessage(player, "That player has already been invited to that company!");
      return;
    }

    Member newMember = Repo.getInstance().createMember(company, playerToInviteObject);
    if (newMember != null) {
      sendMessage(player, "Successfully invited player!");
      sendMessage(playerToInviteObject, "You have been invited to join " + ChatColor.YELLOW + company.name);
    } else {
      sendMessage(player, "Error inviting player!");
    }
  }
}
