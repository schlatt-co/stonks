package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServicesListGui extends CollectionGui<Service> {
  private Company company;

  public ServicesListGui(Company company, Account account) {
    super(account.services, company.name + " services (" + account.name + ")");
    this.company = company;
  }


  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to service folders"), e -> player.performCommand("stonks servicefolders " + company.name)));
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.logoMaterial), company.name)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, Service obj) {
    //Two different sets of actions based on the role of the player in the company
    Member m = company.getMember(player);
    //If the player isnt a manager then allow them to subscribe
    if (m == null || !m.hasManagamentPermission()) {
      //If the player is subscribed tell them, and dont let them subscribe
      if (obj.getSubscription(player) != null) {
        return ClickableItem.of(ItemInfoHelper.serviceDisplayItem(obj,
            ChatColor.GREEN + "You are subscribed!",
            ChatColor.RED + "Left click to unsubscribe",
            ChatColor.LIGHT_PURPLE + "Right click to view subscribers"),
            e -> {
              if (e.getClick().isLeftClick()) {
                player.performCommand("stonks unsubscribe " + obj.pk);
              } else if (e.getClick().isRightClick()) {
                player.performCommand("stonks subscribers " + obj.pk);
              }
            }
        );
        //Else allow the player to subscribe
      } else {
        return ClickableItem.of(ItemInfoHelper.serviceDisplayItem(obj,
            ChatColor.LIGHT_PURPLE + "Left click to subscribe",
            ChatColor.LIGHT_PURPLE + "Right click to view subscribers"),
            e -> {
              if (e.getClick().isLeftClick()) {
                player.performCommand("stonks subscribe " + obj.pk);
              } else if (e.getClick().isRightClick()) {
                player.performCommand("stonks subscribers " + obj.pk);
              }
            }
        );
      }
      //Else show management info and commands
    } else {
      return ClickableItem.of(ItemInfoHelper.serviceDisplayItem(obj,
          ChatColor.LIGHT_PURPLE + "Left click to view info",
          ChatColor.LIGHT_PURPLE + "Right click to view subscribers"),
          e -> {
            if (e.getClick().isLeftClick()) {
              player.performCommand("stonks serviceinfo " + obj.pk);
            } else if (e.getClick().isRightClick()) {
              player.performCommand("stonks subscribers " + obj.pk);
            }
          }
      );
    }


  }
}
