package dev.tycho.stonks.command.stonks.subs.member;

import dev.tycho.stonks.api.event.CompanyJoinEvent;
import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AcceptInviteSubCommand extends ModularSubCommand {
  public AcceptInviteSubCommand() {
    super(new CompanyValidator("company"));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
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
      Bukkit.getPluginManager().callEvent(new CompanyJoinEvent(company, player));
      sendMessage(player, "You are now a member of " + ChatColor.YELLOW + company.name + "!");
      Repo.getInstance().sendMessageToAllOnlineManagers(company, player.getDisplayName() + " just joined " + ChatColor.GOLD + company.name);
    } else {
      sendMessage(player, "Joining this company failed. Please contact an admin");
    }
  }
}
