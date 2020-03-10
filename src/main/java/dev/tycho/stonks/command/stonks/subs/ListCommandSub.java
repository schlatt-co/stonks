package dev.tycho.stonks.command.stonks.subs;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.autocompleters.OptionListAutocompleter;
import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import dev.tycho.stonks.command.base.validators.StringValidator;
import dev.tycho.stonks.gui.CompanyListGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListCommandSub extends ModularCommandSub {

  public ListCommandSub() {
    super(ArgumentValidator.optional(new StringValidator("options")));
    addAutocompleter("options", new OptionListAutocompleter("all", "member-of", "not-hidden", "verified"));
  }

  @Override
  public void execute(Player player) {
    String option = getArgument("options");
    if (option != null) {
      switch (option) {
        case "all":
          openCompanyList(player, CompanyListOptions.ALL);
          return;
        case "member-of":
          openCompanyList(player, CompanyListOptions.MEMBER_OF);
          return;
        case "not-hidden":
          openCompanyList(player, CompanyListOptions.NOT_HIDDEN_OR_MEMBER);
          return;
        case "verified":
          openCompanyList(player, CompanyListOptions.VERIFIED);
          return;
      }
    }

    //default to not hidden
    openCompanyList(player, CompanyListOptions.getDefault());
  }

  private void openCompanyList(Player player, ListCommandSub.CompanyListOptions options) {
    Stonks.newChain()
        .asyncFirst(() -> {
          List<Company> companies;
          switch (options) {
            default:
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
          }
          companies.sort(Comparator.comparing(a -> a.name.toLowerCase()));
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
    MEMBER_OF;

    public static CompanyListOptions getDefault() {
      return NOT_HIDDEN_OR_MEMBER;
    }

  }
}
