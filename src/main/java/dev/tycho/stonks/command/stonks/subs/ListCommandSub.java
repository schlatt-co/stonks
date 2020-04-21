package dev.tycho.stonks.command.stonks.subs;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.OptionListAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.CompanyListGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommandSub extends ModularCommandSub {

  public ListCommandSub() {
    super(ArgumentValidator.optional(new StringValidator("options")));
    addAutocompleter("options", new OptionListAutocompleter("all", "member-of", "not-hidden", "verified", "search"));
  }

  @Override
  public void execute(Player player) {
    String optionArg = getArgument("options");
    CompanyListOptions option = CompanyListOptions.getDefault();
    if (optionArg != null) {
      switch (optionArg.toLowerCase()) {
        case "all": {
          option = CompanyListOptions.ALL;
          break;
        }
        case "member-of": {
          option = CompanyListOptions.MEMBER_OF;
          break;
        }
        case "not-hidden": {
          option = CompanyListOptions.NOT_HIDDEN_OR_MEMBER;
          break;
        }
        case "verified": {
          option = CompanyListOptions.VERIFIED;
          break;
        }
        case "search": {
          option = CompanyListOptions.SEARCH;
          break;
        }
        default: {
          break;
        }
      }
    }
    openCompanyList(player, option);
  }

  private void openCompanyList(Player player, ListCommandSub.CompanyListOptions options) {
    Stonks.newChain()
        .asyncFirst(() -> {
          List<Company> companies = new ArrayList<>();
          switch (options) {
            case ALL:
              companies = new ArrayList<>(Repo.getInstance().companies().getAll());
              break;
            case NOT_HIDDEN_OR_MEMBER:
              companies = new ArrayList<>(Repo.getInstance().companies().getAllWhere(
                  c ->
                      !c.hidden ||
                          c.isMember(
                              player
                          )));
              break;
            case VERIFIED:
              companies = new ArrayList<>(Repo.getInstance().companies().getAllWhere(c -> c.verified));
              break;
            case MEMBER_OF:
              companies = new ArrayList<>(Repo.getInstance().companies().getAllWhere(c -> c.isMember(player)));
              break;
            case SEARCH: {
              Bukkit.getScheduler().runTask(Stonks.getInstance(), () -> new AnvilGUI.Builder()
                  .title("Search for a company...")
                  .text("Type here")
                  .item(new ItemStack(Material.PAPER))
                  .plugin(Stonks.getInstance())
                  .preventClose()
                  .onComplete((opener, search) -> {
                    Bukkit.getScheduler().runTask(Stonks.getInstance(), () -> {
                      List<Company> filterCompanies = new ArrayList<>(Repo.getInstance().companies().getAllWhere(c ->
                          c.name.toLowerCase().contains(search.toLowerCase())));
                      filterCompanies.sort(Comparator.comparing(a -> a.name.toLowerCase()));
                      filterCompanies = filterCompanies.stream().filter(a -> !a.name.equals("_")).collect(Collectors.toList());
                      new CompanyListGui(filterCompanies).show(player);
                    });
                    return AnvilGUI.Response.close();
                  })
                  .open(player));
              return null;
            }
          }
          companies.sort(Comparator.comparing(a -> a.name.toLowerCase()));
          companies = companies.stream().filter(a -> !a.name.equals("_")).collect(Collectors.toList());
          return new CompanyListGui(companies);
        })
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }

  enum CompanyListOptions {
    ALL,
    NOT_HIDDEN_OR_MEMBER,
    VERIFIED,
    MEMBER_OF,
    SEARCH;

    public static CompanyListOptions getDefault() {
      return NOT_HIDDEN_OR_MEMBER;
    }

  }
}
