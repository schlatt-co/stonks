package dev.tycho.stonks.command;

import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.PlayerStateData;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player player = (Player) sender;
    if (args.length == 0 || !PlayerStateData.getInstance().getChatSelectionStore().containsKey(player)) {
      new CompanySelectorGui.Builder()
          .companies(Repo.getInstance().companiesWhereMember(player))
          .title("Select company for chat.")
          .companySelected((company -> {
            PlayerStateData.getInstance().getChatSelectionStore().put(player, company.pk);
            player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "Set " + company.name + " as company chat channel. You may send a message now using /cc <message>");
            if (args.length != 0) {
              sendMessage(player, args);
            }
          }))
          .show(player);
      return true;
    }
    sendMessage(player, args);
    return true;
  }

  private void sendMessage(Player player, String[] args) {
    StringBuilder message = new StringBuilder(player.getDisplayName() + ": ");
    for (String arg : args) {
      message.append(arg).append(" ");
    }
    Repo.getInstance().sendMessageToAllOnlineMembers(Repo.getInstance().companies().get(PlayerStateData.getInstance().getChatSelectionStore().get(player)), message.toString());
  }
}