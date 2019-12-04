package dev.tycho.stonks.command.subs.account;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.AccountListGui;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.db_new.Repo;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AccountsCommandSub extends CommandSub {

  public AccountsCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " accounts <company name>");
      return;
    }


    String companyName = concatArgs(1, args);

    Stonks.newChain()
        .asyncFirst(() -> {
            Company company = Repo.getInstance().companyWithName(companyName);
            if (company == null) {
              sendMessage(player, "That company doesn't exist!");
              return null;
            }
            return new AccountListGui(company);
        })
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
