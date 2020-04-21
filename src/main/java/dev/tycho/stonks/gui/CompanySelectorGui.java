package dev.tycho.stonks.gui;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CompanySelectorGui extends InventoryGui {
  private Consumer<Company> onCompanySelected;
  private ArrayList<Company> allCompanies;
  private Collection<Company> shownCompanies;

  private CompanySelectorGui(Collection<Company> companies, String title, Consumer<Company> onCompanySelected) {
    super(title, 6);
    // Don't show deleted companies
    this.allCompanies = companies.stream().filter(c -> !c.name.equals("_")).collect(Collectors.toCollection(ArrayList::new));
    this.allCompanies.sort(Comparator.comparing(c -> c.name));
    this.shownCompanies = allCompanies.stream()
        .filter(c -> !c.hidden)
        .collect(Collectors.toList());
    this.onCompanySelected = onCompanySelected;
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
    contents.fillRow(5, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));

    Pagination pagination = contents.pagination();
    pagination.setItemsPerPage(36);

    // Default
    contents.set(0, 0, ClickableItem.of(Util.item(Material.GRAY_STAINED_GLASS, "Filter by: Default (hide some companies)"),
        e -> {
          shownCompanies = allCompanies.stream()
              .filter(c -> !c.hidden || c.isMember(player))
              .collect(Collectors.toList());
          getInventory().open(player, 0);
        }));

    // Member of
    contents.set(0, 1, ClickableItem.of(Util.playerHead("Filter by: Member Of", player),
        e -> {
          shownCompanies = allCompanies.stream()
              .filter(c -> c.isMember(player))
              .collect(Collectors.toList());
          getInventory().open(player, 0);
        }));

    // Verified Only
    contents.set(0, 2, ClickableItem.of(Util.item(Material.ENCHANTED_BOOK, "Filter by: Verified"),
        e -> {
          shownCompanies = allCompanies.stream()
              .filter(c -> c.verified)
              .collect(Collectors.toList());
          getInventory().open(player, 0);
        }));

    // All
    contents.set(0, 3, ClickableItem.of(Util.item(Material.GLASS, "Filter by: Show All (incl. hidden)"),
        e -> {
          shownCompanies = new ArrayList<>(allCompanies);
          getInventory().open(player, 0);
        }));

    // Search
    contents.set(0, 4, ClickableItem.of(Util.item(Material.ANVIL, "Search for Company..."),
        e -> new AnvilGUI.Builder()
            .title("Search for a company...")
            .text("Type here")
            .item(new ItemStack(Material.PAPER))
            .plugin(Stonks.getInstance())
            .preventClose()
            .onComplete((opener, search) -> {
              Bukkit.getScheduler().runTask(Stonks.getInstance(), () -> {
                shownCompanies = allCompanies.stream()
                    .filter(c -> c.name.toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
                getInventory().open(player, 0);
              });
              return AnvilGUI.Response.close();
            })
            .open(player)));

    ClickableItem[] items = new ClickableItem[shownCompanies.size()];
    int i = 0;
    for (Company c : shownCompanies) {
      items[i] = itemProvider(player, c);
      i++;
    }
    if (items.length > 0) {
      pagination.setItems(items);
    } else {
      pagination.setItems(ClickableItem.empty(Util.item(Material.COBWEB, "No items")));
    }
    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


    int pageNumber = pagination.getPage() + 1;
    ItemStack pageIndicator = Util.item(Material.BLACK_STAINED_GLASS_PANE, "Page " + pageNumber);
    pageIndicator.setAmount(pageNumber);


    contents.set(5, 4, ClickableItem.empty(pageIndicator));
    //Add pagination arrows unless we only have one page


    if (pagination.isFirst()) {
      if (!pagination.isLast()) contents.set(5, 3, ClickableItem.empty(Util.item(Material.AIR, " ")));
    } else {
      contents.set(5, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
          e -> getInventory().open(player, pagination.previous().getPage())));
    }
    if (pagination.isLast()) {
      if (!pagination.isFirst()) contents.set(5, 5, ClickableItem.empty(Util.item(Material.AIR, " ")));
    } else {
      contents.set(5, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
          e -> getInventory().open(player, pagination.next().getPage())));
    }
  }

  protected void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }

  protected ClickableItem itemProvider(Player player, Company obj) {
    return ClickableItem.of(ItemInfoHelper.companyDisplayItem(obj),
        e -> {
          close(player);
          onCompanySelected.accept(obj);
        });
  }

  public static class Builder {
    private Collection<Company> companies = new ArrayList<>();
    private String title = "";
    private Consumer<Company> onCompanySelected;

    public Builder() {

    }

    public CompanySelectorGui.Builder companies(Collection<Company> companies) {
      this.companies = companies;
      return this;
    }

    public CompanySelectorGui.Builder title(String title) {
      this.title = title;
      return this;
    }

    public CompanySelectorGui.Builder companySelected(Consumer<Company> onCompanySelected) {
      this.onCompanySelected = onCompanySelected;
      return this;
    }

    public CompanySelectorGui show(Player player) {
      CompanySelectorGui companySelectorGui = new CompanySelectorGui(companies, title, onCompanySelected);
      companySelectorGui.show(player);
      return companySelectorGui;
    }
  }


}
