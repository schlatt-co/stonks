package dev.tycho.stonks.command.base.autocompleters;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MemberCompanyNameAutocompleter extends ArgumentAutocompleter {
  @Override
  public List<String> getCompletions(Player player, String arg) {
    List<String> names = new ArrayList<>();
    List<Company> companies = Repo.getInstance().companies().getAllWhere(
        c ->
            c.isMember(player) &&
                (arg.isEmpty() || c.name.toUpperCase().contains(arg.toUpperCase()))
    );
    // Limit the number of companies we return to 10
    for (int i = 0; i < Math.min(20, companies.size()); i++) {
      names.add(companies.get(i).name);
    }
    return names;
  }
}
