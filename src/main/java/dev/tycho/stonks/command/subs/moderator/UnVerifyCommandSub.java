package dev.tycho.stonks.command.subs.moderator;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnVerifyCommandSub extends CommandSub {

  public UnVerifyCommandSub() {
    super("trevor.mod");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    new CompanySelectorGui.Builder()
        .title("Select company to unverify")
        .companies(Repo.getInstance().companies().getAllWhere(c -> c.verified))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Unverify " + company.name + "?")
            .onChoiceMade(c -> {
              if (c)
                Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, false, company.hidden);
            })
            .open(player))
        .open(player);
  }
}
