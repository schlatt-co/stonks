package nl.tychovi.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import nl.tychovi.stonks.Database.Company;
import nl.tychovi.stonks.managers.DatabaseManager;
import nl.tychovi.stonks.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class CompanyListGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    public static SmartInventory getInventory() {
        return SmartInventory.builder()
                .id("companyList")
                .provider(new CompanyListGui())
                .manager(inventoryManager)
                .size(5, 9)
                .title("Company list")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));

        Pagination pagination = contents.pagination();

        List<Company> list = null;
        try {
            list = databaseManager.getCompanyDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ClickableItem[] items = new ClickableItem[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Company company = list.get(i);
            ClickableItem item = ClickableItem.of(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName()),
                    e -> {
                        CompanyInfoGui.getInventory(company).open(player);
                    });
            items[i] = item;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


        contents.set(4, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
                e -> getInventory().open(player, pagination.previous().getPage())));
        contents.set(4, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
                e -> getInventory().open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
