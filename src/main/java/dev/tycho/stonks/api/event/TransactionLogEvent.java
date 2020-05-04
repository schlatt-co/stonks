package dev.tycho.stonks.api.event;

import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.logging.Transaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TransactionLogEvent extends Event {
  private final Company company;
  private final Account account;
  private final Transaction transaction;
  private final Player player;

  public Player getPlayer() {
    return player;
  }

  public Company getCompany() {
    return company;
  }

  public Account getAccount() {
    return account;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  private static final HandlerList handlers = new HandlerList();

  public TransactionLogEvent(Company company, Account account, Transaction transaction, Player player) {
    super(true);
    this.company = company;
    this.account = account;
    this.transaction = transaction;
    this.player = player;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
