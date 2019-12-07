package dev.tycho.stonks.command.subs.member;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.CompanyValidator;
import dev.tycho.stonks.gui.MemberListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MembersCommandSub extends ModularCommandSub {

  public MembersCommandSub() {
    super(new CompanyValidator("company"));
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void execute(Player player) {

    Company company = getArgument("company");
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
