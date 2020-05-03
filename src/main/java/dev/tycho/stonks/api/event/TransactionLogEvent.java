package dev.tycho.stonks.api.event;

import dev.tycho.stonks.model.logging.Transaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TransactionLogEvent extends Event {

  private final Transaction transaction;

  public Transaction getTransaction() {
    return transaction;
  }

  private static final HandlerList handlers = new HandlerList();

  public TransactionLogEvent(Transaction transaction) {
    super(true);
    this.transaction = transaction;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }
}
