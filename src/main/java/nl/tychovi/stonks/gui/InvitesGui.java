package nl.tychovi.stonks.gui;


import nl.tychovi.stonks.Database.Company;
import nl.tychovi.stonks.Database.Member;
import nl.tychovi.stonks.managers.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.SQLException;
import java.util.List;

public class InvitesGui extends Gui {
    public InvitesGui(DatabaseManager databaseManager) {
        super(27, "Invites", databaseManager);
    }

    public void initializeItems(List<Member> invites) {
        inv.clear();
        for(Member invite : invites) {
            String itemName = ChatColor.RESET + "" + ChatColor.BOLD + "" + ChatColor.GOLD + invite.getCompany().getName();
            inv.addItem(createGuiItem(Material.PAPER, itemName, ChatColor.GREEN + "Left click to accept invite.", ChatColor.RED + "Right click to decline invite."));
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        if(e.getClickedInventory() != this.getInventory()) {
            return;
        }

        Boolean response = null;
        if(e.getClick().isLeftClick()) {
            response = true;
        } else if(e.getClick().isRightClick()) {
            response = false;
        } else {
            return;
        }

        try {
            Company company = databaseManager.getCompanyDao().getCompany(e.getCurrentItem().getItemMeta().getDisplayName());
            databaseManager.getMemberDao().handleInvite(response, company.getId(), e.getWhoClicked().getUniqueId());

            if(response) {
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Invitation successfully accepted!");
            } else {
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Invitation successfully declined!");
            }

            e.getWhoClicked().closeInventory();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
