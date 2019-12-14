package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.core.Role;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static dev.tycho.stonks.model.core.Role.CEO;
import static dev.tycho.stonks.model.core.Role.Manager;

public class SetRoleCommandSub extends ModularCommandSub {

  private static final List<String> ROLES = Arrays.asList(
      "CEO",
      "Manager",
      "Employee",
      "Intern");

  public SetRoleCommandSub() {
    super(new StringValidator("player_name"),
        new ArgumentValidator<Role>("role") {
          @Override
          public boolean provide(String str) {
            try {
              value = Role.valueOf(str);
              return true;
            } catch (IllegalArgumentException e) {
              return false;
            }
          }

          @Override
          public String getPrompt() {
            return "must be a valid role (CEO, Manager, Employee, Intern)";
          }
        }, new CompanyValidator("company"));
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return matchPlayerName(args[1]);
    } else if (args.length == 3) {
      return copyPartialMatches(args[2], ROLES);
    }
    return null;
  }

  @Override
  public void execute(Player player) {
    String playerName = getArgument("player_name");
    Role role = getArgument("role");
    Company company = getArgument("company");
    setRole(player, playerName, role, company);
  }

  private void setRole(Player player, String playerName, Role newRole, Company company) {
    //See if the player to promote exists
    Player playerToChange = playerFromName(playerName);
    if (playerToChange == null) {
      sendMessage(player, "That player has never played on the server!");
      return;
    }
    //Now check both are members
    Member changingMember = company.getMember(player);
    if (changingMember == null) {
      sendMessage(player, "You are not a member of that company!");
      return;
    }

    Member memberToChange = company.getMember(playerToChange);
    if (memberToChange == null) {
      sendMessage(player, "That player is not part of that company!");
      return;
    }
    //Both players are a member of the company
    //Now check permissions
    //A player can't change their own role
    if (changingMember.playerUUID.equals(memberToChange.playerUUID)) {
      sendMessage(player, "You cannot change your own role!");
      return;
    }
    if (!changingMember.canChangeRole(memberToChange, newRole)) {
      sendMessage(player, "You do not have the permissions to promote that user to " + ChatColor.YELLOW + newRole.name());
      return;
    }
    //If we are promoting them to a ceo then demote us
    if (newRole == CEO) {
      Repo.getInstance().modifyMember(changingMember, Manager, changingMember.acceptedInvite);
      sendMessage(player, "You promoted " + ChatColor.YELLOW + playerName + ChatColor.GREEN + " to CEO, you have been demoted to a Manager since there can only be one CEO.");
    }
    Repo.getInstance().modifyMember(memberToChange, newRole, memberToChange.acceptedInvite);
    sendMessage(player, "Changed user role successfully!");
    sendMessage(playerToChange, "Your rank in the company " + ChatColor.YELLOW + company.name + ChatColor.GREEN + " has changed to " + ChatColor.YELLOW + newRole.toString());
  }
}
