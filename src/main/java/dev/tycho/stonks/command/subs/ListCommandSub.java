package dev.tycho.stonks.command.subs;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanyListGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class ListCommandSub extends CommandSub {

  public ListCommandSub() {
    super(false);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {

    if (args.length > 1) {
      switch (args[1]) {
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
          Collection<Company> companies;
          switch (options) {
            default:
            case ALL:
              companies = Repo.getInstance().companies().getAll();
              break;
            case NOT_HIDDEN_OR_MEMBER:
              companies = Repo.getInstance().companies().getAllWhere(
                  c ->
                      !c.hidden ||
                          c.isMember(
                              player
                          ));
              break;
            case VERIFIED:
              companies = Repo.getInstance().companies().getAllWhere(c -> c.verified);
              break;
            case MEMBER_OF:
              companies = Repo.getInstance().companies().getAllWhere(c -> c.isMember(player));
              break;
          }
          return new CompanyListGui(companies);
        })
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
