package dev.tycho.stonks.api.event;

import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CompanyJoinEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final Company company;
  private final Player player;

  public CompanyJoinEvent(Company company, Player player) {
    this.company = company;
    this.player = player;
  }

  public Company getCompany() {
    return company;
  }

  public Player getPlayer() {
    return player;
  }

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
