package dev.tycho.stonks.gui;

import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.Company;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CompanySelectorGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    private List<Company> companies;
    private String title;
    private Consumer<Company> onCompanySelected;
    private SmartInventory inventory;

    private SmartInventory getInventory() {
        return SmartInventory.builder()
                .id("CompanySelectorGui")
                .provider(this)
                .manager(inventoryManager)
                .size(5, 9)
                .title(title)
                .build();
    }

    public CompanySelectorGui(List<Company> companies, String title, Consumer<Company> onCompanySelected,  Player player) {
        this.companies = companies;
        this.title = title;
        this.onCompanySelected = onCompanySelected;
        this.inventory = SmartInventory.builder()
                .id("CompanySelectorGui")
                .provider(this)
                .manager(inventoryManager)
                .size(5, 9)
                .title(title)
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        Pagination pagination = contents.pagination();
        ClickableItem[] items = new ClickableItem[companies.size()];

        for (int i = 0; i < companies.size(); i++) {
            Company company = companies.get(i);
            ClickableItem item = ClickableItem.of(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName(), "Total value: " + company.getTotalValue()),
                    e -> {
                        inventory.close(player);
                        onCompanySelected.accept(company);
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

    public static class Builder {
        private List<Company> companies = new ArrayList<>();
        private String title = "";
        private Consumer<Company> onCompanySelected;

        public Builder() {

        }
        public CompanySelectorGui.Builder companies(List<Company> companies) {
            this.companies = companies;
            return this;
        }
        public CompanySelectorGui.Builder title(String title) {
            this.title = title;
            return this;
        }

        public CompanySelectorGui.Builder companySelected(Consumer<Company> onCompanySelected) {
            this.onCompanySelected = onCompanySelected;
            return this;
        }

        public CompanySelectorGui open(Player player) {
            return new CompanySelectorGui(companies, title, onCompanySelected, player);
        }
    }


}
