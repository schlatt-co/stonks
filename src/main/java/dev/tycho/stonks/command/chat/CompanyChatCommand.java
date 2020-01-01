package dev.tycho.stonks.command.chat;

import com.earth2me.essentials.User;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.PlayerData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompanyChatCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player player = (Player) sender;
    if (args.length == 0 || !PlayerData.getInstance().getSelectedCompanyChat().containsKey(player)) {
      new CompanySelectorGui.Builder()
          .companies(Repo.getInstance().companiesWhereMember(player))
          .title("Select company for chat.")
          .companySelected(company -> {
            PlayerData.getInstance().getSelectedCompanyChat().put(player, company.pk);
            player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "Set " + company.name + " as company chat channel. You may send a message now using /cc <message>");
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
    StringBuilder message = new StringBuilder(player.getDisplayName() + ": ");
    for (String arg : args) {
      message.append(arg).append(" ");
    }

    for (Member member : company.members) {
      User u = Stonks.essentials.getUser(member.playerUUID);
      if (u == null) continue;
      Player p = u.getBase();
      if (p == null || !player.isOnline()) continue;
      p.sendMessage(message.toString());

      PlayerData.getInstance().getReplyCompanyChat().put(p, company.pk);
    }
  }
}