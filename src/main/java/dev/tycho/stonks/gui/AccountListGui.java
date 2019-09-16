package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AccountListGui extends CollectionGuiBase<AccountLink> {
  private Company company;

  public AccountListGui(Company company) {
    super(company.getAccounts(), company.getName() + " accounts");
    this.company = company;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"), e -> player.performCommand("stonks info " + company.getName())));
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));
  }

  @Override
  protected ClickableItem itemProvider(Player player, AccountLink obj) {
    Account account = obj.getAccount();
    ReturningAccountVisitor<ClickableItem> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        val = ClickableItem.of(
            ItemInfoHelper.accountDisplayItem(obj, player, ChatColor.YELLOW + "Right click to see history"),
            e -> {
              if (e.getClick().isRightClick()) {
                player.performCommand("stonks history " + obj.getId());
              }
            });
      }

      @Override
      public void visit(HoldingsAccount a) {
        val = ClickableItem.of(
            ItemInfoHelper.accountDisplayItem(obj, player, ChatColor.DARK_PURPLE + "Left click to see holdings", ChatColor.YELLOW + "Right click to see history"),
            e -> {
              if (e.getClick().isLeftClick()) {
                player.performCommand("stonks holdinginfo " + obj.getId());
              } else if (e.getClick().isRightClick()) {
                player.performCommand("stonks history " + obj.getId());
              }
            });
      }
    };
    account.accept(visitor);
    return visitor.getRecentVal();
  }
}
