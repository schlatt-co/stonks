package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.MemberListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MembersCommandSub extends CommandSub {

  public MembersCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " members <company name>");
      return;
    }
    Company company = companyFromName(concatArgs(1, args));
    if (company == null) {
      sendMessage(player, "That company doesn't exist!");
      return;
    }
    Stonks.newChain()
        .asyncFirst(() -> new MemberListGui(company,
            company.members.stream().filter(m -> m.acceptedInvite).collect(Collectors.toList())))
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
