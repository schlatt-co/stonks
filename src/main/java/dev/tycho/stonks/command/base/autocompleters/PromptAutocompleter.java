package dev.tycho.stonks.command.base.autocompleters;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PromptAutocompleter extends ArgumentAutocompleter {
  public final List<String> args;

  public PromptAutocompleter(String prompt) {
    this.args = new ArrayList<>();
    args.add(prompt);
  }

  @Override
  public List<String> getCompletions(Player player, String arg) {
    if (arg.isEmpty()) {
      return args;
    } else {
      return null;
    }
  }
}
