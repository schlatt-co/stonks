package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.MemberInfoGui;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MemberInfoCommandSub extends ModularCommandSub {

  public MemberInfoCommandSub() {
    super(new StringValidator("player_name"), new CompanyValidator("company"));
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    if (args.length == 2) {
      return matchPlayerName(args[1]);
    }
    return null;
  }

  @Override
  public void execute(Player player) {

    Player playerProfile = playerFromName(getArgument("player_name"));
    Company company = getArgument("company");
    Member member = company.getMember(playerProfile);
    if (member == null) {
      sendMessage(player, "That player isn't a member of that company!");
      return;
    }
    MemberInfoGui.getInventory(member, company).open(player);
  }
}
