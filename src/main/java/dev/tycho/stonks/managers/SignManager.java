package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Objects;

public class SignManager extends SpigotModule {
    public SignManager(Stonks plugin) {
        super("signManager", plugin);
    }

    @EventHandler
    public void onSignCreation(SignChangeEvent event) {
        // [Rent]
        // AccountId
        // Price for specified interval
        // Interval (in hours)

        // ACCOUNTID-COMPANYNAME
        // how long left
        // $amount + interval
        // player that is renting

        if(!event.getLine(0).matches("[Rent]") || !event.getLine(0).matches("[rent]")) {
            return;
        }
        if(!event.getLine(2).matches("\\d+") || !(event.getLine(1).substring(1).matches("\\d+"))) {
           event.getPlayer().sendMessage(ChatColor.RED + "Incorrect prefix for a rent sign!");
           return;
        }
        event.setLine(0, ChatColor.AQUA + "" + ChatColor.BOLD + "[Rent]");
        event.setLine(1, event.getLine(1) + "-COMPANYNAMEHERE");
        event.setLine(2, ChatColor.GREEN + "" + "$" + event.getLine(1));
        event.setLine(3, ChatColor.AQUA + event.getLine(3) + " hours");
    }
}
