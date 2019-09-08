package dev.tycho.stonks.command.company;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
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
      sendMessage(player, "Correct usage /" + alias + " services <company_name>" );
      return;
    }
    DatabaseHelper.getInstance().openCompanyServices(player, concatArgs(1, args));

  }
}
