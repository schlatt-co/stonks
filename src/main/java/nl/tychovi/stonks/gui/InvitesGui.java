package nl.tychovi.stonks.gui;


import nl.tychovi.stonks.Database.Member;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class InvitesGui extends Gui {
    public InvitesGui() {
        super(27, "Invites");
    }

    public void initializeItems(List<Member> invites) {
        inv.clear();
        for(Member invite : invites) {
            String itemName = ChatColor.RESET + "" + ChatColor.BOLD + "" + ChatColor.GOLD + invite.getCompany().getName();
            inv.addItem(createGuiItem(Material.PAPER, itemName));
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        if(e.getClickedInventory() != this.getInventory()) {
            return;
        }

        if(e.getClick().isLeftClick()) {
            e.getWhoClicked().sendMessage("Left");
        } else if(e.getClick().isRightClick()) {
            e.getWhoClicked().sendMessage("Right");
        }
    }
}
