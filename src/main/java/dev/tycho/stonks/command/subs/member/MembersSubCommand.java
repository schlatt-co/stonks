package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.command.base.ModularSubCommand;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.MemberListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class MembersSubCommand extends ModularSubCommand {

  public MembersSubCommand() {
    super(new CompanyValidator("company"));
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    new MemberListGui(company, company.members.stream().filter(m -> m.acceptedInvite).collect(Collectors.toList())).show(player);
  }
}
