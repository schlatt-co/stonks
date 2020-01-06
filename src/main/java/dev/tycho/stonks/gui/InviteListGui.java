package dev.tycho.stonks.gui;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

public class InviteListGui extends CollectionGui<Member> {
  public InviteListGui(Collection<Member> invites) {
    super(invites, "Invites Inbox");
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {

  }

  @Override
  protected ClickableItem itemProvider(Player player, Member obj) {
    Company company = Repo.getInstance().companies().get(obj.companyPk);
    if (company == null) {
      player.sendMessage("Error opening GUI, company was null, tell an admin");
      return ClickableItem.empty(Util.item(Material.COBWEB, "error"));
    }
    return ClickableItem.of(Util.item(Material.getMaterial(company.logoMaterial), company.name, ChatColor.GREEN + "Left click to accept.", ChatColor.RED + "Right click to decline."),
        e -> {
          if (e.getClick().isLeftClick()) {
            player.performCommand("stonks acceptinvite " + company.pk);
          } else if (e.getClick().isRightClick()) {
            player.performCommand("stonks declineinvite " + company.pk);
          } else {
            return;
          }
          player.closeInventory();
        });
  }
}
