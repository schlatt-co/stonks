package dev.tycho.stonks.gui;

import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
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

import java.util.Collection;

public class AccountListGui implements InventoryProvider {

  public static DatabaseManager databaseManager;
  public static InventoryManager inventoryManager;

  private Company company;

  public AccountListGui(Company company) {
    this.company = company;
  }

  public static SmartInventory getInventory(Company company) {
    return SmartInventory.builder()
        .id("memberList")
        .provider(new AccountListGui(company))
        .manager(inventoryManager)
        .size(5, 9)
        .title(company.getName() + " accounts")
        .build();
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    Collection<AccountLink> links = company.getAccounts();
    contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"), e -> player.performCommand("stonks info " + company.getName())));

    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));
//        contents.set(4, 1, ClickableItem.empty(Util.item(Material.CHEST, ChatColor.AQUA + "Chestshop how-to", "To use a company account instead of your personal balance", "to manage a chestshop", "put '#ACCOUNTIDHERE'", "on the first line instead of your username.", "The rest is the same.", "You can get the id from an account on this page.")));


    Pagination pagination = contents.pagination();

    ClickableItem[] items = new ClickableItem[links.size()];


    int i = 0;
    for (AccountLink link : links) {
      Account account = link.getAccount();
      ReturningAccountVisitor<ClickableItem> visitor = new ReturningAccountVisitor<>() {
        @Override
        public void visit(CompanyAccount a) {
          val = ClickableItem.of(
              ItemInfoHelper.accountDisplayItem(link, ChatColor.YELLOW + "Right click to see history"),
              e -> {
                if (e.getClick().isRightClick()) {
                  player.performCommand("stonks history " + link.getId());
                }
              });
        }

        @Override
        public void visit(HoldingsAccount a) {
          val = ClickableItem.of(
              ItemInfoHelper.accountDisplayItem(link, ChatColor.DARK_PURPLE + "Left click to see holdings", ChatColor.YELLOW + "Right click to see history"),
              e -> {
                if (e.getClick().isLeftClick()) {
                  player.performCommand("stonks holdinginfo " + link.getId());
                } else if (e.getClick().isRightClick()) {
                  player.performCommand("stonks history " + link.getId());
                }
              });
        }
      };
      account.accept(visitor);
      items[i] = visitor.getRecentVal();
      i++;
    }

    pagination.setItems(items);
    pagination.setItemsPerPage(27);

    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


    contents.set(4, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
        e -> getInventory(company).open(player, pagination.previous().getPage())));
    contents.set(4, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
        e -> getInventory(company).open(player, pagination.next().getPage())));

  }

  @Override
  public void update(Player player, InventoryContents contents) {

  }
}
