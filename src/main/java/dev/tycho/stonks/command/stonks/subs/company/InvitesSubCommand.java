package dev.tycho.stonks.command.stonks.subs.company;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.command.base.SubCommand;
import dev.tycho.stonks.gui.InviteListGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.entity.Player;

import java.util.Collection;

public class InvitesSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    Collection<Member> invites = Repo.getInstance().getInvites(player);
    if (invites == null || invites.size() == 0) {
      SubCommand.sendMessage(player, "You don't have any pending invites!");
      return;
    }
    new InviteListGui(invites).show(player);
  }
}
