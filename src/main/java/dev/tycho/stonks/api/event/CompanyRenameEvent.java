package dev.tycho.stonks.api.event;

import dev.tycho.stonks.model.core.Company;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CompanyRenameEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final Company company;
  private final String newName;

  public CompanyRenameEvent(Company company, String newName) {
    this.company = company;
    this.newName = newName;
  }

  public Company getCompany() {
    return company;
  }

  public String getNewName() {
    return newName;
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
