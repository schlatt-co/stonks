package dev.tycho.stonks.managers;

import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.*;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.perks.ChestShopPerk;
import dev.tycho.stonks.util.StonksUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.UUID;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;


public class ShopManager extends SpigotModule {

  public ShopManager(Stonks plugin) {
    super("Shop Manager", plugin);
    PerkManager.getInstance().registerPerk(new ChestShopPerk(plugin));
  }

  @EventHandler
  public void onPreShopCreation(PreShopCreationEvent event) {
    String accountLine = event.getSignLine(NAME_LINE);
    String priceLine = event.getSignLine(PRICE_LINE);
    if (!accountLine.startsWith("#")) {
      return;
    }

    int accountId = 0;
    if (accountLine.substring(1).matches("\\d+")) {
      accountId = Integer.parseInt(accountLine.substring(1));
    } else if (accountLine.contains("-")) {
      int indexOfDash = accountLine.indexOf("-");
      if (accountLine.substring(1, indexOfDash).matches("\\d+")) {
        accountId = Integer.parseInt(accountLine.substring(1, indexOfDash));
      }
    } else {
      return;
    }

    //If an account exists for this account id
    dev.tycho.stonks.model.core.Account account;
    if ((account = Repo.getInstance().accountWithId(accountId)) != null) {
      Company company = Repo.getInstance().companies().get(account.companyPk);
      if (!company.ownsPerk(ChestShopPerk.class)) {
        sendMessage(event.getPlayer(), "Your company didn't buy the ChestShop Integration perk! You'll need to buy this perk before you can make ChestShops of companies!");
        event.setOutcome(PreShopCreationEvent.CreationOutcome.NO_PERMISSION);
        return;
      }
      //If the sign is a sell sign don't allow holdings accounts
      //todo turn this into a visitor and remove the need for getAccountType
      if (priceLine.toLowerCase().contains("s") && account instanceof HoldingsAccount) {
        event.setOutcome(PreShopCreationEvent.CreationOutcome.INVALID_PRICE);
        sendMessage(event.getPlayer(), "You cannot create a sell sign for a holdings account! " + account.name + " (id " + account.pk + ") is a holdings account");
        return;
      }
      Member member = company.getMember(event.getPlayer());
      if (member == null || member.role.equals(Role.Intern) || !member.acceptedInvite) {
        event.setOutcome(PreShopCreationEvent.CreationOutcome.NO_PERMISSION);
      }
    }

  }

  @EventHandler
  public void onAccountQuery(AccountQueryEvent event) {
    String accountLine = event.getName();
    if (!accountLine.startsWith("#")) {
      return;
    }
    int accountId;
    if (accountLine.substring(1).matches("\\d+")) {
      accountId = Integer.parseInt(accountLine.substring(1));
    } else if (accountLine.contains("-")) {
      int indexOfDash = accountLine.indexOf("-");
      accountId = Integer.parseInt(accountLine.substring(1, indexOfDash));
    } else {
      return;
    }

    //If an account exists with this id
    dev.tycho.stonks.model.core.Account account;
    if ((account = Repo.getInstance().accountWithId(accountId)) != null) {
      UUID accountUuid = account.uuid;
      //Get the chest shop account associated with this uuid
      Account CSaccount = NameManager.getAccount(accountUuid);
      Company company = Repo.getInstance().companies().get(account.companyPk);

      if (CSaccount == null) {
        //If none exists then make a new one
        String name = company.name;
        Account newCSaccount = new Account("#" + accountId + "-" + name, accountUuid);
        event.setAccount(newCSaccount);
      } else {
        event.setAccount(CSaccount);
      }
    }
  }

  @EventHandler
  public void onAccountCheck(PreAccountCheckEvent event) {
    String accountName = event.getAccount().getName();

    if (!accountName.startsWith("#")) {
      return;
    }

    int dashIndex = accountName.indexOf("-");
    if (dashIndex == -1) {
      return;
    }

    int accountId = Integer.parseInt(accountName.substring(1, dashIndex));
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithId(accountId);
    if (account != null) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onOwnEvent(AccountOwnerCheckEvent event) {
    if (!event.getName().startsWith("#")) {
      return;
    }
    int dashIndex = event.getName().indexOf("-");
    if (dashIndex == -1) {
      event.setCancelled(true);
      return;
    }
    int accountId = Integer.parseInt(event.getName().substring(1, dashIndex));
    //Find the account for this id
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithId(accountId);

    if (account == null) {
      //Account id doesn't exist
      return;
    }

    //Check if the player is a member of this company
    Company company = Repo.getInstance().companies().get(account.companyPk);
    Member member = company.getMember(event.getPlayer());

    //Return if the member does not have access permissions
    if (member == null || member.role.equals(Role.Intern) || !member.acceptedInvite) {
      return;
    }

    //If the member is an owner of the account then cancel
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEconomyCheck(AccountCheckEvent event) {
    if (event.hasAccount()) {
      return;
    }

    //Try to see if we have an account for this UUID
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithUUID(event.getAccount());
    if (account != null) {
      event.hasAccount(true);
    } else if (!event.getAccountObj().getShortName().startsWith("#")) {
      try {
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getAccount());
        if (player.hasPlayedBefore()) {
          event.hasAccount(Stonks.economy.createPlayerAccount(player));
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  @EventHandler
  public void onCurrencyAdd(PreCurrencyAddEvent event) {
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithUUID(event.getTarget());
    if (account != null) {
      event.setCancelled(true);
      Repo.getInstance().payAccount(null, "shop transaction", account, event.getAmountSent().doubleValue());
    }
  }

  @EventHandler
  public void onCurrencySubtract(PreCurrencySubtractEvent event) {
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithUUID(event.getSender());
    if (account != null) {
      event.setCancelled(true);
      IAccountVisitor visitor = new IAccountVisitor() {
        @Override
        public void visit(CompanyAccount a) {
          if (a.balance < event.getAmountSent().doubleValue()) {
            event.setBalanceSufficient(false);
          } else {
            //We have enough money
            Repo.getInstance().withdrawFromAccount(null, a, event.getAmountSent().doubleValue());
            //Transaction success
          }
        }

        @Override
        public void visit(HoldingsAccount a) {
          //A holdings account can't pay out
          System.out.println("Tried to pay out of a holdings account using a shop sign");
          System.out.println("Holdings account ID: " + a.pk);
          event.setBalanceSufficient(false);
        }
      };
      account.accept(visitor);
    }
  }

  @EventHandler
  public void onAmountCheck(PreAmountCheckEvent event) {
    //If they try to check an account balance for one of our accounts cancel it
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithUUID(event.getAccount());
    if (account != null) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onCurrencyCheck(PreCurrencyCheckEvent event) {
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithUUID(event.getAccount());
    if (account != null) {
      if (account.getTotalBalance() >= event.getAmountSent().doubleValue()) {
        event.setHasEnough(true);
      }
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onOfflinePlayerBuyEvent(OfflinePlayerBuyEvent event) {
    if (!event.getTransactionEvent().getOwnerAccount().getName().startsWith("#")) {
      event.setCancelled(true);
      return;
    }
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithId(Integer.parseInt(event.getTransactionEvent().getOwnerAccount().getName().split("-")[0].replaceFirst("#", "")));
    if (account == null) {
      event.setCancelled(true);
      return;
    }
    Company company = Repo.getInstance().companies().get(account.companyPk);
    for (Member member : company.members) {
      if (member.hasManagamentPermission()) {
        StonksUser user = Stonks.getUser(member.playerUUID);
        if (user.getBase() == null) {
          continue;
        }
        Player player = user.getBase();
        if (player.isOnline()) {
          event.addTarget(player);
        }
      }
    }
  }

  @EventHandler
  public void onOfflinePlayerSellEvent(OfflinePlayerSellEvent event) {
    if (!event.getTransactionEvent().getOwnerAccount().getName().startsWith("#")) {
      event.setCancelled(true);
      return;
    }
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithId(Integer.parseInt(event.getTransactionEvent().getOwnerAccount().getName().split("-")[0].replaceFirst("#", "")));
    if (account == null) {
      event.setCancelled(true);
      return;
    }
    Company company = Repo.getInstance().companies().get(account.companyPk);
    for (Member member : company.members) {
      if (member.hasManagamentPermission()) {
        Player player = Stonks.getUser(member.playerUUID).getBase();
        if (player != null && player.isOnline()) {
          event.addTarget(player);
        }
      }
    }
  }

  @EventHandler
  public void onOfflinePlayerErrorMessageEvent(OfflinePlayerErrorMessageEvent event) {
    if (!event.getAccount().getName().startsWith("#")) {
      event.setCancelled(true);
      return;
    }
    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithId(Integer.parseInt(event.getAccount().getName().split("-")[0].replaceFirst("#", "")));
    if (account == null) {
      event.setCancelled(true);
      return;
    }
    Company company = Repo.getInstance().companies().get(account.companyPk);
    for (Member member : company.members) {
      if (member.hasManagamentPermission()) {
        StonksUser user = Stonks.getUser(member.playerUUID);
        if (user.getBase() == null) {
          return;
        }
        Player player = user.getBase();
        if (player.isOnline()) {
          event.addTarget(player);
        }
      }
    }
  }
}
