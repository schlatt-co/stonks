package dev.tycho.stonks.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import dev.tycho.stonks.model.Company;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.util.Util;
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
        contents.set(0,0,ClickableItem.of(Util.item(Material.BARRIER, "Company List"), e -> player.performCommand("stonks list")));

        contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));
        contents.set(2, 3, ClickableItem.of(Util.item(Material.PLAYER_HEAD, "Members"), e -> player.performCommand("stonks members " + company.getName())));
        contents.set(2, 5, ClickableItem.of(Util.item(Material.GOLD_BLOCK, "Accounts"), e -> player.performCommand("stonks accounts " + company.getName())));

        //If the player is a member of the company with management permissions then we can show an edit option


    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
