package dev.tycho.stonks.command;

import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.PlayerStateData;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;

public class ChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        HashMap<Player, Integer> chatSelectionStore = PlayerStateData.getInstance().getChatSelectionStore();
        if(args.length < 1 || !chatSelectionStore.containsKey(player)) {
            Collection<Company> list = Repo.getInstance().companiesWhereMember(player);
            new CompanySelectorGui.Builder()
                    .companies(list)
                    .title("Select company for chat.")
                    .companySelected((company -> {
                        chatSelectionStore.put(player, company.pk);
                        player.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + "Set " + company.name + " as company chat channel. You may send a message now using /cc <message>");
                    }))
                    .show(player);
        } else {
            StringBuilder message = new StringBuilder(player.getDisplayName() + ": ");
            for (String arg : args) {
                message.append(arg).append(" ");
            }
            Repo.getInstance().sendMessageToAllOnlineMembers(Repo.getInstance().companies().get(chatSelectionStore.get(player)), message.toString());
        }
        return true;
    }
}
