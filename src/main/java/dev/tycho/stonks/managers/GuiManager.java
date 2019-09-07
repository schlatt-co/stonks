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
        CollectionGuiBase.databaseManager = databaseManager;
        CollectionGuiBase.inventoryManager = inventoryManager;

        CompanyInfoGui.inventoryManager = inventoryManager;
        CompanyInfoGui.databaseManager = databaseManager;

        ServiceInfoGui.inventoryManager = inventoryManager;
        ServiceInfoGui.databaseManager = databaseManager;

        MemberInfoGui.databaseManager = databaseManager;
        MemberInfoGui.inventoryManager = inventoryManager;


        AccountTypeSelectorGui.inventoryManager = inventoryManager;
        AccountTypeSelectorGui.databaseManager = databaseManager;

        ConfirmationGui.inventoryManager = inventoryManager;
        ConfirmationGui.databaseManager = databaseManager;
    }
}