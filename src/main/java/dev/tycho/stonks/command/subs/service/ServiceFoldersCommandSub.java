package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.ServiceFoldersListGui;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ServiceFoldersCommandSub extends CommandSub {
  public ServiceFoldersCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2) {
      sendMessage(player, "Correct usage /" + alias + " servicefolders <company_name>");
      return;
    }
    Company company = companyFromName(concatArgs(1, args));

    if (company == null) {
      sendMessage(player, "That company doesn't exist!");
      return;
    }
    Stonks.newChain()
        .asyncFirst(() -> new ServiceFoldersListGui(company))
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
