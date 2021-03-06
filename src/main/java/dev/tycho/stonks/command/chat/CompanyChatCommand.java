package dev.tycho.stonks.command.chat;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.PlayerData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.perks.CompanyChatPerk;
import dev.tycho.stonks.util.StonksUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CompanyChatCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    Player player = (Player) sender;

    //If we need to select a company to chat in
    if (args.length == 0 || !PlayerData.getInstance().getSelectedCompanyChat().containsKey(player)) {
      //Show a company selector
      new CompanySelectorGui.Builder()
          .companies(Repo.getInstance().companies().getAllWhere(
              p -> p.ownsPerk(CompanyChatPerk.class) && p.isMember(player)))
          .title("Select company for chat.")
          .companySelected(company -> {
            PlayerData.getInstance().getSelectedCompanyChat().put(player, company.pk);
            player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "Set " + company.name + " as company chat channel. You may send a message now using /cc <message>");
            //If there is a message to send as well then send that
            if (args.length != 0) {
              messageMembers(player, company, args);
            }
          })
          .show(player);
      return true;
    }
    Company company = Repo.getInstance().companies().get(PlayerData.getInstance().getSelectedCompanyChat().get(player));
    messageMembers(player, company, args);
    return true;
  }

  private void messageMembers(Player player, Company company, String[] args) {
    if (!company.ownsPerk(CompanyChatPerk.class)) {
      player.sendMessage(ChatColor.GREEN + "Stonks> That company hasn't bought the company chat perk");
      return;
    }
    StringBuilder message = new StringBuilder(ChatColor.GREEN + company.name + "> " + ChatColor.WHITE).append(player.getDisplayName()).append(": ");
    for (String arg : args) {
      message.append(arg).append(" ");
    }

    for (Member member : company.members) {
      if (member.acceptedInvite) {
        StonksUser u = Stonks.getUser(member.playerUUID);
        Player p = u.getBase();
        if (p == null || !player.isOnline()) continue;
        p.sendMessage(message.toString());
        PlayerData.getInstance().getReplyCompanyChat().put(p, company.pk);
      }
    }
  }
}