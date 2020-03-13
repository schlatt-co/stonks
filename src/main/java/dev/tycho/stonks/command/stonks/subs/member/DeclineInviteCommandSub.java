package dev.tycho.stonks.command.stonks.subs.member;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.CompanyNameAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentStore;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.entity.Player;

public class DeclineInviteCommandSub extends ModularCommandSub {


  public DeclineInviteCommandSub() {
    super(new CompanyValidator("company"));
    addAutocompleter("company", new CompanyNameAutocompleter());
  }

  @Override
  public void execute(Player player, ArgumentStore store) {
    Company company = getArgument("company", store);
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
    if (Repo.getInstance().deleteMember(member)) {
      sendMessage(player, "Successfully rejected the invite!");
    } else {
      sendMessage(player, "Error rejecting invite. Please contact an admin");
    }
  }
}
