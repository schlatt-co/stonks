package dev.tycho.stonks.command.base.autocompleters;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CompanyNameAutocompleter extends ArgumentAutocompleter {
  @Override
  public List<String> getCompletions(Player player, String arg) {
    List<String> names = new ArrayList<>();
    List<Company> companies = Repo.getInstance().companies().getAllWhere(c->c.isMember(player));
    for (int i = 0; i < Math.min(10, companies.size()); i++) {
      names.add(companies.get(i).name);
    }
    return names;
  }
}
