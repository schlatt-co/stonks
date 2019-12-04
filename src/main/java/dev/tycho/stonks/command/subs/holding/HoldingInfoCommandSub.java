package dev.tycho.stonks.command.subs.holding;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.gui.HoldingListGui;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.HoldingsAccount;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HoldingInfoCommandSub extends CommandSub {

  public HoldingInfoCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length == 1) {
      sendMessage(player, "Correct usage: " + ChatColor.YELLOW + "/" + alias + " holdinginfo <account id>");
      return;
    }

    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Invalid account id!");
      return;
    }

    Account account = Repo.getInstance().accountWithId(Integer.parseInt(args[1]));
    if (account == null) {
      sendMessage(player, "Account with id does not exist!");
      return;
    }
    Stonks.newChain()
        .asyncFirst(() -> {
            if (!(account instanceof HoldingsAccount)) {
              sendMessage(player, "You can only view holdings of holding accounts!");
              return null;
            }
            return new HoldingListGui((HoldingsAccount)account);
        })
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
