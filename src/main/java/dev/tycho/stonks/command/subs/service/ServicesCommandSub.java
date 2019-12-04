package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.gui.ServicesListGui;
import dev.tycho.stonks.model.core.Account;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ServicesCommandSub extends CommandSub {

  public ServicesCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2){
      sendMessage(player, "Correct usage /" + alias + " services <account_id>" );
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Correct user: " + ChatColor.YELLOW + "/" + alias + " services <account id>");
      return;
    }
    Account account = Repo.getInstance().accountWithId(Integer.parseInt(args[1]));
    if (account == null) {
      sendMessage(player, "Account id not found");
      return;
    }

    Stonks.newChain()
        .asyncFirst(() -> new ServicesListGui(Repo.getInstance().companies().get(account.companyPk), account))
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }
}
