package dev.tycho.stonks.command.stonks.subs.company;

import dev.tycho.stonks.command.base.SimpleCommandSub;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.InviteListGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.entity.Player;

import java.util.Collection;

public class InvitesCommandSub extends SimpleCommandSub {

  @Override
  public void execute(Player player) {
    Collection<Member> invites = Repo.getInstance().getInvites(player);
    if (invites == null || invites.size() == 0) {
      CommandSub.sendMessage(player, "You don't have any pending invites!");
      return;
    }
    new InviteListGui(invites).show(player);
  }
}
