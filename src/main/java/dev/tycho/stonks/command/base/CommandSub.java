package dev.tycho.stonks.command.base;

import dev.tycho.stonks.db_new.Repo;
import dev.tycho.stonks.model.core.Company;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class CommandSub {

  private final String permission;

  private final boolean autoComplete;

  public CommandSub() {
    this(null, true);
  }

  public CommandSub(boolean autoComplete) {
    this(null, autoComplete);
  }

  public CommandSub(String permission) {
    this(permission, true);
  }

  public CommandSub(String permission, boolean autoComplete) {
    this.permission = permission;
    this.autoComplete = autoComplete;
  }

  public abstract List<String> onTabComplete(CommandSender sender, String alias, String[] args);

  public abstract void onCommand(Player player, String alias, String[] args);

  protected final void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  protected boolean validateDouble(String text) {
    return Pattern.matches("([0-9]*)\\.?([0-9]*)?", text);
  }

  protected String concatArgs(int startArg, String[] args) {
    StringBuilder concat = new StringBuilder();
    for (int i = startArg; i < args.length; i++) {
      if (i > startArg) concat.append(" ");
      concat.append(args[i]);
    }
    return concat.toString();
  }

  protected List<String> copyPartialMatches(String search, Iterable<String> stack) {
    List<String> matches = new ArrayList<>();
    StringUtil.copyPartialMatches(search, stack, matches);
    return matches;
  }

  protected List<String> matchPlayerName(String search) {
    List<String> playerNames = new ArrayList<>();
    Bukkit.getOnlinePlayers().forEach(o -> playerNames.add(o.getName()));
    return copyPartialMatches(search, playerNames);
  }

  protected Company companyFromName(String name) {
    Company company;
    if (StringUtils.isNumeric(name)) {
      //Numeric name means we have a company ID
      company = Repo.getInstance().companies().get(Integer.parseInt(name));
    } else {
      //String name means get value for the name
      company = Repo.getInstance().companyWithName(name);
    }
    return company;
  }

  String getPermission() {
    return permission;
  }

  boolean isAutoComplete() {
    return autoComplete;
  }
}
