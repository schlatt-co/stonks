package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class InviteListGui extends CollectionGuiBase<Member> {

  public InviteListGui(Player player) {
    super(databaseManager.getMemberDao().getInvites(player), "Invites Inbox");
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {

  }

  @Override
  protected ClickableItem itemProvider(Player player, Member obj) {
    Company company = obj.getCompany();
    return ClickableItem.of(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName(), ChatColor.GREEN + "Left click to accept.", ChatColor.RED + "Right click to decline."),
        e -> {
          boolean accepted;
          if (e.getClick().isLeftClick()) {
            accepted = true;
            player.sendMessage(ChatColor.GREEN + "Invite successfully accepted!");
          } else if (e.getClick().isRightClick()) {
            player.sendMessage(ChatColor.GREEN + "Invite successfully declined!");
            accepted = false;
          } else {
            return;
          }
          try {
            databaseManager.getMemberDao().handleInvite(accepted, company.getId(), player.getUniqueId());
          } catch (SQLException ex) {
            ex.printStackTrace();
          }
          player.closeInventory();
        });
  }
}
