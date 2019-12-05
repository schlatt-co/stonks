package dev.tycho.stonks.command.subs.company;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.gui.InviteListGui;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class InvitesCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    Stonks.newChain()
        .asyncFirst(() -> {
          Collection<Member> invites = Repo.getInstance().getInvites(player);
          if (invites == null || invites.size() == 0) {
            sendMessage(player, "You don't have any pending invites!");
            return null;
          }
          return new InviteListGui(invites);
        })
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
