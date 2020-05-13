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

public class HoldingListGui extends CollectionGui<Holding> {
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
  protected ClickableItem itemProvider(Player player, Holding h) {
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(h.playerUUID);
    double percentage = (h.share / holdingsAccount.getTotalShare()) * 100;
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.WHITE + "Balance: " + ChatColor.GREEN + "$" + Util.commify(h.balance));
    lore.add(ChatColor.WHITE + "Share: " + ChatColor.YELLOW + h.share + ChatColor.ITALIC + " (" + String.format("%.2f", percentage) + "%)");
    if (player.getUniqueId().equals(h.playerUUID))
      lore.add(ChatColor.GREEN + "Left click to withdraw your holding.");
    lore.add(ChatColor.RED + "Right click to delete holding.");

    return ClickableItem.of(Util.playerHead(offlinePlayer.getName(), offlinePlayer,
        lore), e -> {
      if (e.getClick().isRightClick()) {
        player.performCommand("stonks removeholdinguuid " + holdingsAccount.pk + " " + offlinePlayer.getUniqueId().toString());
      } else if (e.getClick().isLeftClick() && player.getUniqueId().equals(h.playerUUID)) {
        player.performCommand("stonks withdraw " + h.balance + " " + holdingsAccount.pk);
      }
    });
  }
}
