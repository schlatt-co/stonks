package dev.tycho.stonks.managers;

import dev.tycho.stonks.gui.*;
import fr.minuskube.inv.InventoryManager;
import dev.tycho.stonks.Stonks;

public class GuiManager extends SpigotModule {

    private InventoryManager inventoryManager;
    private DatabaseManager databaseManager;

    public GuiManager(Stonks plugin) {
        super("guiManager", plugin);
        this.inventoryManager = new InventoryManager(plugin);
        inventoryManager.init();
        databaseManager = (DatabaseManager) plugin.getModule("databaseManager");
    }

    @Override
    public void enable() {
        InviteListGui.inventoryManager = inventoryManager;
        InviteListGui.databaseManager = databaseManager;

        CompanyListGui.inventoryManager = inventoryManager;
        CompanyListGui.databaseManager = databaseManager;

        CompanyInfoGui.inventoryManager = inventoryManager;
        CompanyInfoGui.databaseManager = databaseManager;

        MemberListGui.inventoryManager = inventoryManager;
        MemberListGui.databaseManager = databaseManager;

        AccountListGui.inventoryManager = inventoryManager;
        AccountListGui.databaseManager = databaseManager;

        MemberInfoGui.databaseManager = databaseManager;
        MemberInfoGui.inventoryManager = inventoryManager;

        HoldingListGui.databaseManager = databaseManager;
        HoldingListGui.inventoryManager = inventoryManager;
    }
}