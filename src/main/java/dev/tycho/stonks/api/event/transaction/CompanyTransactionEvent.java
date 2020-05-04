package dev.tycho.stonks.api.event.transaction;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CompanyTransactionEvent extends Event implements Cancellable {
  boolean cancelled = false;
  private static final HandlerList handlers = new HandlerList();

  private double amount;
  private ITransactionUser sender;
  private ITransactionUser recipient;
  private TransactionType transactionType;


  public double getAmount() {
    return amount;
  }

  public ITransactionUser getRecipient() {
    return recipient;
  }

  public ITransactionUser getSender() {
    return sender;
  }

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public CompanyTransactionEvent(ITransactionUser sender, ITransactionUser recipient, double amount, TransactionType type) {
    this.sender = sender;
    this.recipient = recipient;
    this.amount = amount;
    this.transactionType = type;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    cancelled = cancel;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    SHOP_BUY,
    SHOP_SELL,
    UNKNOWN
  }


}
