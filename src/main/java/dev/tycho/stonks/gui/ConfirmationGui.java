package dev.tycho.stonks.gui;

import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConfirmationGui extends InventoryGui {
  private List<String> info;
  private Runnable onYes;
  private Runnable onNo;

  //turn this consumer into two consumers.
  public ConfirmationGui(Runnable onYes, Runnable onNo, String title, List<String> info) {
    super(title, 3);
    this.onYes = onYes;
    this.onNo = onNo;
    this.info = info;
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    contents.set(1, 3, ClickableItem.of(Util.item(Material.GREEN_WOOL, "YES"),
        e -> {
          close(player);
          onYes.run();
        }));
    contents.set(1, 5, ClickableItem.of(Util.item(Material.RED_WOOL, "NO"),
        e -> {
          close(player);
          onNo.run();
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
    private Runnable onYes = () -> {
    };
    private Runnable onNo = () -> {
    };

    public Builder() {

    }

    public ConfirmationGui.Builder yes(Runnable consumer) {
      this.onYes = consumer;
      return this;
    }

    public ConfirmationGui.Builder no(Runnable consumer) {
      this.onNo = consumer;
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
      new ConfirmationGui(onYes, onNo, title, info).show(player);
    }
  }


}
