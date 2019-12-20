package dev.tycho.stonks.command.subs.moderator;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.gui.ConfirmationGui;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnHideCommandSub extends CommandSub {

  public UnHideCommandSub() {
    super("trevor.mod");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    new CompanySelectorGui.Builder()
        .title("Select company to unhide")
        .companies(Repo.getInstance().companies().getAllWhere(c -> c.hidden))
        .companySelected(company -> new ConfirmationGui.Builder()
            .title("Unhide " + company.name + "?")
            .yes(() -> Repo.getInstance().modifyCompany(company, company.name, company.logoMaterial, company.verified, false))
            .show(player))
        .show(player);
  }
}
