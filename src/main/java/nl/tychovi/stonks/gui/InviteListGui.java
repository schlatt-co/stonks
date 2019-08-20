package nl.tychovi.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import nl.tychovi.stonks.Database.Company;
import nl.tychovi.stonks.Database.Member;
import nl.tychovi.stonks.managers.DatabaseManager;
import nl.tychovi.stonks.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class InviteListGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    public static SmartInventory getInventory() {
        return SmartInventory.builder()
                .id("inviteList")
                .provider(new InviteListGui())
                .manager(inventoryManager)
                .size(3, 9)
                .title("Invite inbox")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.fillRow(2, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));

        Pagination pagination = contents.pagination();

        List<Member> list = null;
        try {
            list = databaseManager.getMemberDao().getInvites(player);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ClickableItem[] items = new ClickableItem[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Company company = list.get(i).getCompany();
            ClickableItem item = ClickableItem.of(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName(), ChatColor.GREEN + "Left click to accept.", ChatColor.RED + "Right click to decline."),
                    e -> {
                        boolean accepted;
                        if(e.getClick().isLeftClick()) {
                            accepted = true;
                            player.sendMessage(ChatColor.GREEN + "Invite successfully accepted!");
                        } else if(e.getClick().isRightClick()) {
                            player.sendMessage(ChatColor.GREEN + "Invite successfully declined!");
                            accepted = false;
                        } else {
                            return;
                        }
                        try {
                            databaseManager.getMemberDao().handleInvite(accepted, company.getId(), player.getUniqueId());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        player.closeInventory();
                    });
            items[i] = item;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(9);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


        contents.set(2, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
                e -> getInventory().open(player, pagination.previous().getPage())));
        contents.set(2, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
                e -> getInventory().open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
