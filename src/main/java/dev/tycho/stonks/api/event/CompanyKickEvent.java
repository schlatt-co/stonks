package dev.tycho.stonks.api.event;

import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CompanyKickEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final Company company;
  private final Player kickedPlayer;

  public CompanyKickEvent(Company company, Player kickedPlayer) {
    this.company = company;
    this.kickedPlayer = kickedPlayer;
  }

  public Company getCompany() {
    return company;
  }

  public Player getKickedPlayer() {
    return kickedPlayer;
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
