package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.Member;
import dev.tycho.stonks.model.Role;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MemberInfoGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    private Member member;
    private OfflinePlayer offlinePlayer;

    public MemberInfoGui(Member member) {
        this.member = member;
        this.offlinePlayer = Bukkit.getOfflinePlayer(member.getUuid());
    }

    public static SmartInventory getInventory(Member member) {
        return SmartInventory.builder()
                .id("memberInfo")
                .provider(new MemberInfoGui(member))
                .manager(inventoryManager)
                .size(3, 9)
                .title(Bukkit.getOfflinePlayer(member.getUuid()).getName())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.set(0,0,ClickableItem.of(Util.item(Material.BARRIER, "back to memberlist"), e -> player.performCommand("stonks members " + member.getCompany().getName())));

        contents.set(0, 4, ClickableItem.empty(Util.playerHead(offlinePlayer.getName(), offlinePlayer, "Role: " + member.getRole().toString())));

        contents.set(1, 3, ClickableItem.of(Util.item(Material.DIRT, "Set role to intern"), e -> player.performCommand("stonks setrole " + offlinePlayer.getName() + " " + member.getCompany().getName() + " " + Role.Intern.toString())));
        contents.set(1, 2, ClickableItem.of(Util.item(Material.IRON_BLOCK, "Set role to employee"), e -> player.performCommand("stonks setrole " + offlinePlayer.getName() + " " + member.getCompany().getName() + " " + Role.Employee.toString())));
        contents.set(1, 1, ClickableItem.of(Util.item(Material.GOLD_BLOCK, "Set role to manager"), e -> player.performCommand("stonks setrole " + offlinePlayer.getName() + " " + member.getCompany().getName() + " " + Role.Manager.toString())));
        contents.set(1, 0, ClickableItem.of(Util.item(Material.DIAMOND_BLOCK, "Set role to CEO"), e -> player.performCommand("stonks setrole " + offlinePlayer.getName() + " " + member.getCompany().getName() + " " + Role.CEO.toString())));

        contents.set(1, 8, ClickableItem.of(Util.item(Material.BARRIER, ChatColor.RED + "Fire member from company"), e -> player.performCommand("stonks kickmember " + offlinePlayer.getName() + " " + member.getCompany().getName())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
