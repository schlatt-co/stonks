package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HoldingListGui extends CollectionGuiBase<Holding> {
  private HoldingsAccount holdingsAccount;

  public HoldingListGui(HoldingsAccount holdingsAccount) {
    super(holdingsAccount.holdings, holdingsAccount.name + " holdings");
    this.holdingsAccount = holdingsAccount;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"), e -> player.performCommand("stonks accounts " + holdingsAccount.companyPk)));
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.GOLD_INGOT, holdingsAccount.name)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, Holding obj) {
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(obj.player);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.WHITE + "Balance: " + ChatColor.GREEN + "$" + Util.commify(obj.balance));
    lore.add(ChatColor.WHITE + "Share: " + ChatColor.YELLOW + obj.share);
    if (player.getUniqueId().equals(obj.player))
      lore.add(ChatColor.GREEN + "Left click to withdraw your holding.");
    lore.add(ChatColor.RED + "Right click to delete holding.");

    return ClickableItem.of(Util.playerHead(offlinePlayer.getName(), offlinePlayer,
        lore), e -> {
      if (e.getClick().isRightClick()) {
        player.performCommand("stonks removeholding " + holdingsAccount.pk + " " + offlinePlayer.getName());
      } else if (e.getClick().isLeftClick() && player.getUniqueId().equals(obj.player)) {
        player.performCommand("stonks withdraw " + obj.balance + " " + holdingsAccount.pk);
      }
    });
  }
}
