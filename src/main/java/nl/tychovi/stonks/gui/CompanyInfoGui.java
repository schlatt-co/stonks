package nl.tychovi.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import nl.tychovi.stonks.Database.Company;
import nl.tychovi.stonks.managers.DatabaseManager;
import nl.tychovi.stonks.managers.MemberListGui;
import nl.tychovi.stonks.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CompanyInfoGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    private Company company;

    public CompanyInfoGui(Company company) {
        this.company = company;
    }

    public static SmartInventory getInventory(Company company) {
        return SmartInventory.builder()
                .id("companyInfo")
                .provider(new CompanyInfoGui(company))
                .manager(inventoryManager)
                .size(5, 9)
                .title(company.getName())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));

        contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));
        contents.set(2, 3, ClickableItem.of(Util.item(Material.PLAYER_HEAD, "Members"), e -> {
            MemberListGui.getInventory(company).open(player);
        }));
        contents.set(2, 5, ClickableItem.empty(Util.item(Material.GOLD_BLOCK, "Accounts")));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
