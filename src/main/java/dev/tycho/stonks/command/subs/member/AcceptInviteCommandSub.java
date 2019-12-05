package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AcceptInviteCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " acceptinvite <company name>");
      return;
    }

    String name = concatArgs(1, args);
    Company company = companyFromName(name);
    if (company == null) {
      sendMessage(player, "That company does not exist!");
      return;
    }

    Member member = company.getMember(player);
    if (member == null) {
      sendMessage(player, "You have no invite for this company");
      return;
    }

    if (member.acceptedInvite) {
      sendMessage(player, "You have already accepted your invite for this company");
      return;
    }

    //Ok accept the invite
    member = Repo.getInstance().modifyMember(member, member.role, true);
    if (member.acceptedInvite) {
      sendMessage(player, "You are now a member of " + ChatColor.YELLOW + company.name + "!");
    } else {
      sendMessage(player, "Joining this company failed. Please contact an admin");
    }
  }
}
