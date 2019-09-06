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

import java.util.List;

public class MemberListGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    private Company company;

    private List<Member> list;

    public MemberListGui(Company company, List<Member> members) {
        this.company = company;
        this.list = members;
    }

    public static SmartInventory getInventory(Company company, List<Member> members) {
        return SmartInventory.builder()
                .id("memberList")
                .provider(new MemberListGui(company, members))
                .manager(inventoryManager)
                .size(5, 9)
                .title(company.getName() + " Members")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.set(0,0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"), e -> player.performCommand("stonks info " + company.getName())));

        contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Member member = list.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member.getUuid());
            ClickableItem item = ClickableItem.of(Util.playerHead(offlinePlayer.getName(), offlinePlayer, "Role: " + member.getRole().toString()), e -> player.performCommand("stonks memberinfo " + offlinePlayer.getName() + " " + company.getName()));
            items[i] = item;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


        contents.set(4, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
                e -> getInventory(company, list).open(player, pagination.previous().getPage())));
        contents.set(4, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
                e -> getInventory(company, list).open(player, pagination.next().getPage())));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
