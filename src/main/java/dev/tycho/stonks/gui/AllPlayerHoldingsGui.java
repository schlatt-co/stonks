package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.AccountLink;
import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllPlayerHoldingsGui extends CollectionGuiBase<HoldingsAccount> {
  private Player player;
  HashMap<HoldingsAccount, AccountLink> linkLookup = new HashMap<>();

  public AllPlayerHoldingsGui(Player player) {
    super(databaseManager.getHoldingsAccountDao().playerHoldingsAccounts(player), "All your holdings");
    this.player = player;
    for (HoldingsAccount holdingsAccount : getReadonlyCollection()) {
      linkLookup.put(holdingsAccount, databaseManager.getAccountLinkDao().getAccountLink(holdingsAccount));
    }
  }


  @Override
  protected void customInit(Player player, InventoryContents contents) {
  }

  @Override
  protected ClickableItem itemProvider(Player player, HoldingsAccount obj) {
    Holding holding = obj.getPlayerHolding(player.getUniqueId());
    if (holding == null || !linkLookup.containsKey(obj) || linkLookup.get(obj) == null) {
      return ClickableItem.empty(Util.item(Material.COBWEB, "Error :/"));
    }

    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.WHITE + "Balance: " + ChatColor.GREEN + "$" + Util.commify(holding.getBalance()));
    lore.add(ChatColor.WHITE + "Share: " + ChatColor.YELLOW + holding.getShare());
    lore.add(ChatColor.GREEN + "Left click to withdraw your holding.");
    lore.add(ChatColor.GREEN + "Right click to view this company");



    Material material;
    //If the player has no money in the holding display it as an iron bar
    Holding playerHolding = obj.getPlayerHolding(player.getUniqueId());
    if (playerHolding != null && playerHolding.getBalance() > 0.1) {
      material = Material.GOLD_INGOT;
    } else {
      material = Material.IRON_INGOT;
    }

    return ClickableItem.of(Util.item(material, obj.getName(),
        lore), e -> {
      if (e.getClick().isLeftClick()) {
        player.performCommand("stonks withdraw " + holding.getBalance() + " " + linkLookup.get(obj).getId());
      } else if (e.getClick().isRightClick()) {
        player.performCommand("stonks info " + linkLookup.get(obj).getCompany().getName());
      }
    });
  }
}
