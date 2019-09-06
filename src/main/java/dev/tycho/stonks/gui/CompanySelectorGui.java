package dev.tycho.stonks.gui;

import dev.tycho.stonks.model.core.Company;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class CompanySelectorGui extends CollectionGuiBase<Company> {
  private Consumer<Company> onCompanySelected;

  private CompanySelectorGui(Collection<Company> companies, String title, Consumer<Company> onCompanySelected) {
    super(companies, title);
    this.onCompanySelected = onCompanySelected;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {

  }

  @Override
  protected ClickableItem itemProvider(Player player, Company obj) {
    return ClickableItem.of(ItemInfoHelper.companyDisplayItem(obj),
        e -> {
          close(player);
          onCompanySelected.accept(obj);
        });
  }

  public static class Builder {
    private List<Company> companies = new ArrayList<>();
    private String title = "";
    private Consumer<Company> onCompanySelected;

    public Builder() {

    }

    public CompanySelectorGui.Builder companies(List<Company> companies) {
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

    public CompanySelectorGui open(Player player) {
      CompanySelectorGui companySelectorGui = new CompanySelectorGui(companies, title, onCompanySelected);
      companySelectorGui.show(player);
      return companySelectorGui;
    }
  }


}
