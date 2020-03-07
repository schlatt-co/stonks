package dev.tycho.stonks.command.base.autocompleters;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionListAutocompleter extends ArgumentAutocompleter {
  public final List<String> args;

  public OptionListAutocompleter(String... args) {
    this.args = Arrays.asList(args);
  }

  public OptionListAutocompleter(List<String> args) {
    this.args = new ArrayList<>(args);
  }

  @Override
  public List<String> getCompletions(Player player, String arg) {
    return args;
  }
}
