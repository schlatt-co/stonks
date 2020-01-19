package dev.tycho.stonks.newgui;

import dev.tycho.stonks.gui.InventoryGui;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Gui extends InventoryGui {

  Gui(List<Page> pages) {
    super("title", 5);
    this.pages = pages;
  }

  List<Page> pages;
  Page currentPage;
  Page nextPage;

  @Override
  public void init(Player player, InventoryContents contents) {
    currentPage = null;
    nextPage = pages.get(0);
  }

  @Override
  public void update(Player player, InventoryContents contents) {
    if (currentPage != nextPage) {
      //Clear all
      contents.fill(ClickableItem.empty(Util.item(Material.AIR, "")));
      nextPage.draw(player, contents);
      currentPage = nextPage;
    }
  }
}
