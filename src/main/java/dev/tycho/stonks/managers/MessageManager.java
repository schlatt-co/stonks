package dev.tycho.stonks.managers;

import dev.tycho.stonks.model.Member;
import dev.tycho.stonks.Stonks;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.List;

public class MessageManager extends SpigotModule {
    private DatabaseManager databaseManager;

    public MessageManager(Stonks plugin) {
        super("MessageManager", plugin);
        this.databaseManager = (DatabaseManager) plugin.getModule("databaseManager");
    }

    public static void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.AQUA + "--------------------");
        player.sendMessage(ChatColor.GOLD + "/stonks create <company> - Create a company with the specified name.");
        player.sendMessage(ChatColor.GOLD + "/stonks list - A list with all companies and their total value.");
        player.sendMessage(ChatColor.GOLD + "/stonks invite <player> <company> - Invite a player to your company.");
        player.sendMessage(ChatColor.GOLD + "/stonks invites - View your invites.");
        player.sendMessage(ChatColor.AQUA + "--------------------");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        List<Member> invites = databaseManager.getMemberDao().getInvites(event.getPlayer());
        if(invites != null) {
            event.getPlayer().sendMessage(ChatColor.AQUA + "You have " + ChatColor.GREEN + invites.size() + ChatColor.AQUA + " open company invites! Do " + ChatColor.GREEN + "/stonks invites" + ChatColor.AQUA + " to view them.");
        }
    }
}
