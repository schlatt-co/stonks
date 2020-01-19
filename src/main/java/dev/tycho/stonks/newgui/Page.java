package dev.tycho.stonks.newgui;

import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public abstract class Page {
  public Consumer<Page> back;
  public Consumer<Page> close;
  protected final BorderType borderType;
  public void hook(Consumer<Page> back, Consumer<Page> close) {
    this.back = back;
    this.close = close;
  }

  public Page(BorderType borderType) {
    this.borderType = borderType;
  }

  public Page() {
    this.borderType = BorderType.All;
  }

  void init(Player player, InventoryContents contents) {
    //First draw the borders
    switch (borderType) {
      case None:
        break;
      case TopBottom:
        contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.fillRow(contents.inventory().getRows(), ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        break;
      case All:
        contents.fillBorders(ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        break;
    }
    //Then draw a back icon
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back"),
        e -> back()));
    //Now allow subclasses to draw
    draw(player, contents);
  }

  protected abstract void draw(Player player, InventoryContents contents);



  protected void close() {
    if (close != null) {
      close.accept(this);
    }
  }

  protected void back() {
    if (back != null) {
      back.accept(this);
    }
  }

  protected enum BorderType {
    None,
    TopBottom,
    All
  }

}
