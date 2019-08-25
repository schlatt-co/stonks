package dev.tycho.stonks.gui;

import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.Account;
import dev.tycho.stonks.model.AccountLink;
import dev.tycho.stonks.model.Company;
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

import java.util.List;

public class AccountListGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    private Company company;

    private List<Account> list;

    public AccountListGui(Company company, List<Account> companyAccounts) {
        this.company = company;
        this.list = companyAccounts;
    }

    public static SmartInventory getInventory(Company company, List<Account> members) {
        return SmartInventory.builder()
                .id("memberList")
                .provider(new AccountListGui(company, members))
                .manager(inventoryManager)
                .size(5, 9)
                .title(company.getName() + " accounts")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.set(0,0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"), e -> player.performCommand("stonks info " + company.getName())));

        contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));
        contents.set(4, 1, ClickableItem.empty(Util.item(Material.CHEST, ChatColor.AQUA + "Chestshop how-to", "To use a company account instead of your personal balance", "to manage a chestshop", "put '#ACCOUNTIDHERE'", "on the first line instead of your username.", "The rest is the same.", "You can get the id from an account on this page.")));


        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[list.size()];

//        for (int i = 0; i < list.size(); i++) {
//            Account account = list.get(i);
//            AccountLink link = databaseManager.getAccountLinkDao().getAccountLink(account);
//            Material displayMaterial;
//            switch (link.getAccountType()) {
//                case HoldingsAccount:
//                    displayMaterial = Material.GOLD_INGOT;
//                    break;
//                case CompanyAccount:
//                    displayMaterial = Material.DIAMOND;
//                    break;
//                default:
//                    displayMaterial = Material.IRON_INGOT;
//                    break;
//            }
//            ClickableItem item = ClickableItem.empty(Util.item(displayMaterial, account.getName(), "ID: " + link.getId()));
//            items[i] = item;
//        }

        int i = 0;
        for (AccountLink link : company.getAccounts()) {
            Account account = link.getAccount();
            Material displayMaterial;
            switch (link.getAccountType()) {
                case HoldingsAccount:
                    displayMaterial = Material.GOLD_INGOT;
                    break;
                case CompanyAccount:
                    displayMaterial = Material.DIAMOND;
                    break;
                default:
                    displayMaterial = Material.IRON_INGOT;
                    break;
            }
            ClickableItem item = ClickableItem.empty(Util.item(displayMaterial, account.getName(), "ID: " + link.getId(), "Balance: €" + account.getTotalBalance()));
            items[i] = item;
            i++;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


        contents.set(4, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
                e -> getInventory(company, list).open(player, pagination.previous().getPage())));
        contents.set(4, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
                e -> getInventory(company, list).open(player, pagination.next().getPage())));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
