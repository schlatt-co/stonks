package dev.tycho.stonks.command.stonks.subs.member;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.MemberListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class MembersCommandSub extends ModularCommandSub {

  public MembersCommandSub() {
    super(new CompanyValidator("company"));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Company company = getArgument("company");
    new MemberListGui(company, company.members.stream().filter(m -> m.acceptedInvite).collect(Collectors.toList())).show(player);
  }
}
