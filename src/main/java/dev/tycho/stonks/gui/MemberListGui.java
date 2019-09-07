package dev.tycho.stonks.gui;

import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class MemberListGui extends CollectionGuiBase<Member> {

    private Company company;
    public MemberListGui(Company company, List<Member> members) {
        super(members, company.getName() + " Members");
        this.company = company;
    }

    @Override
    protected void customInit(Player player, InventoryContents contents) {
        contents.set(0,0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"),
            e -> player.performCommand("stonks info " + company.getName())));
        contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()),
            company.getName())));
    }

    @Override
    protected ClickableItem itemProvider(Player player, Member obj) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(obj.getUuid());
        return ClickableItem.of(Util.playerHead(offlinePlayer.getName(), offlinePlayer, "Role: " +
            obj.getRole().toString()), e -> player.performCommand("stonks memberinfo " + offlinePlayer.getName() + " " + company.getName()));
    }
}
