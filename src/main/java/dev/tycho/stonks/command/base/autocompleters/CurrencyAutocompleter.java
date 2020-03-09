package dev.tycho.stonks.command.base.autocompleters;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAutocompleter extends ArgumentAutocompleter {

  @Override
  public List<String> getCompletions(Player player, String arg) {
    ArrayList<String> list = new ArrayList<>();
    String prefix = arg.isEmpty() ? "1" : arg;
    list.add(prefix);
    list.add(prefix + "0");
    list.add(prefix + "00");
    list.add(prefix + "000");
    return list;
  }
}
