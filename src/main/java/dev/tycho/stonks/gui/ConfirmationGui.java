package dev.tycho.stonks.gui;

import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfirmationGui extends InventoryGui {
  private Consumer<Boolean> onSelection;
  private List<String> info;

  //turn this consumer into two consumers.
  public ConfirmationGui(Consumer<Boolean> onSelection, String title, List<String> info) {
    super(title);
    this.onSelection = onSelection;
    this.info = info;
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    contents.set(1, 3, ClickableItem.of(Util.item(Material.GREEN_WOOL, "YES"),
        e -> {
          close(player);
          onSelection.accept(true);
        }));
    contents.set(1, 5, ClickableItem.of(Util.item(Material.RED_WOOL, "NO"),
        e -> {
          close(player);
          onSelection.accept(false);
        }));

    if (info.size() > 0) {
      contents.set(0, 0, ClickableItem.empty(Util.item(Material.PAPER, info.get(0), info.subList(1, info.size()))));
    }
  }

  @Override
  public void update(Player player, InventoryContents contents) {

  }

  public static class Builder {
    private String title = "Confirm";
    private List<String> info = new ArrayList<>();
    private Consumer<Boolean> onSelected = e -> {
    };

    public Builder() {

    }

    public ConfirmationGui.Builder onChoiceMade(Consumer<Boolean> onSelected) {
      this.onSelected = onSelected;
      return this;
    }

    public ConfirmationGui.Builder title(String title) {
      this.title = title;
      return this;
    }

    public ConfirmationGui.Builder info(List<String> info) {
      this.info = info;
      return this;
    }

    public void show(Player player) {
      new ConfirmationGui(onSelected, title, info).show(player);
    }
  }


}
