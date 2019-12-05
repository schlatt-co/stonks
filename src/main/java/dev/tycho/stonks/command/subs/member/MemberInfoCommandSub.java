package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.MemberInfoGui;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MemberInfoCommandSub extends CommandSub {

  public MemberInfoCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return matchPlayerName(args[1]);
    }
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 3) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " memberinfo <player> <company>");
      return;
    }
    Stonks.newChain()
        .asyncFirst(() -> {
            Player playerProfile = playerFromName(args[1]);
            Company company = companyFromName(concatArgs(2, args));

            if (playerProfile == null) {
              sendMessage(player, "Player not found");
              return null;
            }

            if (company == null) {
              sendMessage(player, "Company not found");
              return null;
            }

            Member member = company.getMember(playerProfile);
            if (member == null) {
              sendMessage(player, "That player isn't a member of that company!");
              return null;
            }
            return MemberInfoGui.getInventory(member, company);
        })
        .abortIfNull()
        .sync((result) -> result.open(player))
        .execute();
  }
}
