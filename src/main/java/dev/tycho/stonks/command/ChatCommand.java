package dev.tycho.stonks.command;

import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.PlayerStateData;
import dev.tycho.stonks.managers.Repo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player player = (Player) sender;
    HashMap<Player, Integer> chatSelectionStore = PlayerStateData.getInstance().getChatSelectionStore();
    Integer companyPk = PlayerStateData.getInstance().getChatSelectionStore().computeIfAbsent(player, p -> {
      AtomicInteger selected = new AtomicInteger(-1);
      new CompanySelectorGui.Builder()
          .companies(Repo.getInstance().companiesWhereMember(player))
          .title("Select company for chat.")
          .companySelected((company -> {
            selected.set(company.pk);
            player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "Set " + company.name + " as company chat channel. You may send a message now using /cc <message>");
          }))
          .show(player);
      return selected.get();
    });

    if (companyPk == -1) {
      player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "You failed to select a company!");
      return true;
    }

    StringBuilder message = new StringBuilder(player.getDisplayName() + ": ");
    for (String arg : args) {
      message.append(arg).append(" ");
    }
    Repo.getInstance().sendMessageToAllOnlineMembers(Repo.getInstance().companies().get(chatSelectionStore.get(player)), message.toString());

    return true;
  }
}
