package dev.tycho.stonks.command.subs.holding;

import dev.tycho.stonks.command.base.ModularCommandSub;
import dev.tycho.stonks.command.base.validators.AccountValidator;
import dev.tycho.stonks.gui.HoldingListGui;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.HoldingsAccount;
import org.bukkit.entity.Player;

public class HoldingInfoCommandSub extends ModularCommandSub {


  public HoldingInfoCommandSub() {
    super(new AccountValidator("account"));
  }

  @Override
  public void execute(Player player) {
    Account account = getArgument("account");
    if (!(account instanceof HoldingsAccount)) {
      sendMessage(player, "You can only view holdings of holding accounts!");
      return;
    }
    new HoldingListGui((HoldingsAccount) account).show(player);
  }
}
