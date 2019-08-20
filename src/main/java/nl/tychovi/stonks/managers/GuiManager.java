package nl.tychovi.stonks.managers;

import fr.minuskube.inv.InventoryManager;
import nl.tychovi.stonks.Stonks;
import nl.tychovi.stonks.gui.CompanyInfoGui;
import nl.tychovi.stonks.gui.CompanyListGui;
import nl.tychovi.stonks.gui.InviteListGui;

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
    }
}