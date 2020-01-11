package dev.tycho.stonks.api.perks;

import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Role;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CompanyPerkAction {

  private final String name;
  private final Material icon;
  private final Role permissionLevel;
  private final String[] description;

  public CompanyPerkAction(String name, Material icon, Role permissionLevel, String... description) {
    this.name = name;
    this.icon = icon;
    this.permissionLevel = permissionLevel;
    this.description = description;
  }

  public abstract void onExecute(Company company, Player executor);

  public String getName() {
    return name;
  }

  public Material getIcon() {
    return icon;
  }

  public Role getPermissionLevel() {
    return permissionLevel;
  }

  public String[] getDescription() {
    return description;
  }

  private void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.DARK_GREEN + "Stonks> " + ChatColor.GREEN + message);
  }
}
