package dev.tycho.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import dev.tycho.stonks.model.Company;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class CompanyListGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    private List<Company> list;

    public static SmartInventory getInventory(List<Company> companyList) {
        return SmartInventory.builder()
                .id("companyList")
                .provider(new CompanyListGui(companyList))
                .manager(inventoryManager)
                .size(5, 9)
                .title("Company list")
                .build();
    }

    public CompanyListGui(List<Company> companyList) {
        this.list = companyList;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Company company = list.get(i);
            ClickableItem item = ClickableItem.of(ItemInfoHelper.companyDisplayItem(company),
                    e -> {
                        CompanyInfoGui.getInventory(company).open(player);
                        player.performCommand("stonks info " + company.getName());
                    });
            items[i] = item;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


        contents.set(4, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
                e -> getInventory(list).open(player, pagination.previous().getPage())));
        contents.set(4, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
                e -> getInventory(list).open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
