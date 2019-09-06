package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.gui.*;
import fr.minuskube.inv.InventoryManager;

public class GuiManager extends SpigotModule {

    private InventoryManager inventoryManager;
    private DatabaseManager databaseManager;

    public GuiManager(Stonks plugin) {
        super("guiManager", plugin);
        this.inventoryManager = new InventoryManager(plugin);
        inventoryManager.init();
        databaseManager = (DatabaseManager) plugin.getModule("databaseManager");
    }

    //TODO turn these into singletons
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

        CompanySelectorGui.inventoryManager = inventoryManager;
        CompanySelectorGui.databaseManager = databaseManager;

        AccountSelectorGui.inventoryManager = inventoryManager;
        AccountSelectorGui.databaseManager = databaseManager;

        AccountTypeSelectorGui.inventoryManager = inventoryManager;
        AccountTypeSelectorGui.databaseManager = databaseManager;

        ConfirmationGui.inventoryManager = inventoryManager;
        ConfirmationGui.databaseManager = databaseManager;

        TransactionHistoryGui.inventoryManager = inventoryManager;
        TransactionHistoryGui.databaseManager = databaseManager;
    }
}