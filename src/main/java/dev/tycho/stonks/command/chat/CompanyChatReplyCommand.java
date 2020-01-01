package dev.tycho.stonks.command.chat;

import dev.tycho.stonks.managers.PlayerData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompanyChatReplyCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player player = (Player) sender;
    //Make sure the player has a company chat to reply to
    if (!PlayerData.getInstance().getSelectedCompanyChat().containsKey(player)) {
      player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "You have no company chats to reply to.");
      return true;
    }

    if (args.length == 0) {
      player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "You need to type a message");
      return true;
    }

    //Set the selected chat company to the company to reply to
    Company company = Repo.getInstance().companies().get(PlayerData.getInstance().getReplyCompanyChat().get(player));
    PlayerData.getInstance().getSelectedCompanyChat().put(player, company.pk);

    StringBuilder newCommand = new StringBuilder("cc ");
    for (String arg : args) {
      newCommand.append(arg).append(" ");
    }

    //Perform a /cc with the newly selected company chat
    player.performCommand(newCommand.toString());
    return true;
  }
}