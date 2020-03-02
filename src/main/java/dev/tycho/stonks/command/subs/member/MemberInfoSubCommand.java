package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.MemberInfoGui;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.entity.Player;

public class MemberInfoSubCommand extends ModularSubCommand {

  public MemberInfoSubCommand() {
    super(new StringValidator("player_name"), new CompanyValidator("company"));
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
    new MemberInfoGui(member, company).show(player);
  }
}
