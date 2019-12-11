package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.gui.*;
import fr.minuskube.inv.InventoryManager;

public class GuiManager extends SpigotModule {

  private InventoryManager inventoryManager;

  public GuiManager(Stonks plugin) {
    super("GUI Manager", plugin);
    this.inventoryManager = new InventoryManager(plugin);
    inventoryManager.init();
  }

  //TODO turn these into singletons
  @Override
  public void enable() {
    CollectionGuiBase.inventoryManager = inventoryManager;

    CompanyInfoGui.inventoryManager = inventoryManager;

    ServiceInfoGui.inventoryManager = inventoryManager;

    MemberInfoGui.inventoryManager = inventoryManager;

    AccountTypeSelectorGui.inventoryManager = inventoryManager;

    ConfirmationGui.inventoryManager = inventoryManager;
  }
}