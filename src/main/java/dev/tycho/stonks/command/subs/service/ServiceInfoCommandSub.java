package dev.tycho.stonks.command.subs.service;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.gui.ServiceInfoGui;
import dev.tycho.stonks.model.service.Service;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ServiceInfoCommandSub extends CommandSub {

  public ServiceInfoCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    if (args.length < 2) {
      sendMessage(player, "Correct usage /" + alias + " serviceinfo <service_id>");
      return;
    }
    if (!StringUtils.isNumeric(args[1])) {
      sendMessage(player, "Correct usage /" + alias + " serviceinfo <service_id>");
      return;
    }

    Service service = Repo.getInstance().services().get(Integer.parseInt(args[1]));
    if (service == null) {
      player.sendMessage("Service id not found");
      return;
    }
    ServiceInfoGui.getInventory(service, Repo.getInstance().accountWithId(service.accountPk)).open(player);
  }
}
