package dev.tycho.stonks.gui;

import com.j256.ormlite.dao.ForeignCollection;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.*;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class AccountSelectorGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    private Company company;
    private String title;
    private Consumer<AccountLink> onAccountSelected;

    private SmartInventory inventory;

    private SmartInventory getInventory() {
        return SmartInventory.builder()
                .id("AccountSelectorGui")
                .provider(this)
                .manager(inventoryManager)
                .size(5, 9)
                .title(title)
                .build();
    }

    public AccountSelectorGui(Company company, String title, Consumer<AccountLink> onAccountSelected, Player player) {
        this.company = company;
        this.title = title;
        this.onAccountSelected = onAccountSelected;

        this.inventory = SmartInventory.builder()
                .id("AccountSelectorGui")
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

        contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[company.getAccounts().size()];
        int i = 0;
        for (AccountLink link : company.getAccounts()) {
            Account account = link.getAccount();
            ClickableItem item = null;

            //Get an item to display
            ReturningAccountVisitor visitor = new ReturningAccountVisitor() {
                @Override
                public void visit(CompanyAccount a) {
                    val = Material.DIAMOND;
                }

                @Override
                public void visit(HoldingsAccount a) {
                    val = Material.GOLD_INGOT;
                }
            };
            account.accept(visitor);
            //Create an item that triggers the onAccountSelected consumer
            item = ClickableItem.of(Util.item((Material)visitor.getRecentVal(), account.getName(),
                    "ID: " + link.getId(), "Balance: €" + account.getTotalBalance()),
                    e -> {
                        inventory.close(player);
                        onAccountSelected.accept(link);
                    });

            items[i] = item;
            i++;
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
        private Company company = null;
        private String title = "";
        private Consumer<AccountLink> onAccountSelected;

        public Builder() {

        }
        public AccountSelectorGui.Builder company(Company company) {
            this.company = company;
            return this;
        }
        public AccountSelectorGui.Builder title(String title) {
            this.title = title;
            return this;
        }

        public AccountSelectorGui.Builder accountSelected(Consumer<AccountLink> onAccountSelected) {
            this.onAccountSelected = onAccountSelected;
            return this;
        }

        public AccountSelectorGui open(Player player) {
            return new AccountSelectorGui(company, title, onAccountSelected, player);
        }
    }


}
