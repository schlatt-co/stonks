package dev.tycho.stonks.gui;

import com.j256.ormlite.dao.ForeignCollection;
import dev.tycho.stonks.model.*;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HoldingListGui implements InventoryProvider {

  public static DatabaseManager databaseManager;
  public static InventoryManager inventoryManager;

  private HoldingsAccount holdingsAccount;

  public HoldingListGui(HoldingsAccount holdingsAccount) {
    this.holdingsAccount = holdingsAccount;
  }

  public static SmartInventory getInventory(HoldingsAccount holdingsAccount) {
    return SmartInventory.builder()
        .id("holdingList")
        .provider(new HoldingListGui(holdingsAccount))
        .manager(inventoryManager)
        .size(5, 9)
        .title(holdingsAccount.getName() + " info")
        .build();
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    AccountLink link = databaseManager.getAccountLinkDao().getAccountLink(holdingsAccount);

    contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"), e -> player.performCommand("stonks accounts " + link.getCompany().getName())));

    contents.set(0, 4, ClickableItem.empty(Util.item(Material.GOLD_INGOT, holdingsAccount.getName())));


    ForeignCollection<Holding> holdings = holdingsAccount.getHoldings();

    Pagination pagination = contents.pagination();

    ClickableItem[] items = new ClickableItem[holdings.size()];

    int i = 0;
    for (Holding holding : holdings) {
      OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(holding.getPlayer());

      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.WHITE + "Balance: " + ChatColor.GREEN + "$" + Util.commify(holding.getBalance()));
      lore.add(ChatColor.WHITE + "Share: " + ChatColor.YELLOW + holding.getShare());
      if (player.getUniqueId().equals(holding.getPlayer()))
        lore.add(ChatColor.GREEN + "Left click to withdraw your holding.");
      lore.add(ChatColor.RED + "Right click to delete holding.");


      ClickableItem item = ClickableItem.of(Util.playerHead(offlinePlayer.getName(), offlinePlayer,
          lore), e -> {
        if (e.getClick().isRightClick()) {
          player.performCommand("stonks removeholding " + link.getId() + " " + offlinePlayer.getName());
        } else if (e.getClick().isLeftClick() && player.getUniqueId().equals(holding.getPlayer())) {
          player.performCommand("stonks withdraw " + holding.getBalance() + " " + link.getId());
        }
      });
      items[i] = item;
      i++;
    }

    pagination.setItems(items);
    pagination.setItemsPerPage(27);

    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


    contents.set(4, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
        e ->

            getInventory(holdingsAccount).

                open(player, pagination.previous().

                    getPage())));
    contents.set(4, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
        e ->

            getInventory(holdingsAccount).

                open(player, pagination.next().

                    getPage())));

  }

  @Override
  public void update(Player player, InventoryContents contents) {

  }
}
