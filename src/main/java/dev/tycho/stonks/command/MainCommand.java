package dev.tycho.stonks.command;

import dev.tycho.stonks.command.base.CommandBase;
import dev.tycho.stonks.command.subs.FeesSubCommand;
import dev.tycho.stonks.command.subs.HelpSubCommand;
import dev.tycho.stonks.command.subs.ListSubCommand;
import dev.tycho.stonks.command.subs.TopSubCommand;
import dev.tycho.stonks.command.subs.account.*;
import dev.tycho.stonks.command.subs.company.*;
import dev.tycho.stonks.command.subs.holding.CreateHoldingSubCommand;
import dev.tycho.stonks.command.subs.holding.HoldingInfoSubCommand;
import dev.tycho.stonks.command.subs.holding.MyHoldingsSubCommand;
import dev.tycho.stonks.command.subs.holding.RemoveHoldingSubCommand;
import dev.tycho.stonks.command.subs.member.*;
import dev.tycho.stonks.command.subs.moderator.*;
import dev.tycho.stonks.command.subs.service.*;
import dev.tycho.stonks.command.subs.service.subscription.*;

public class MainCommand extends CommandBase {

  public MainCommand() {
    super(new ListSubCommand());
    addSubCommand("accounts", new AccountsSubCommand());
    addSubCommand("acceptinvite", new AcceptInviteSubCommand());
    addSubCommand("createaccount", new CreateAccountSubCommand());
    addSubCommand("create", new CreateSubCommand());
    addSubCommand("createholding", new CreateHoldingSubCommand());
    addSubCommand("declineinvite", new DeclineInviteSubCommand());
    addSubCommand("fees", new FeesSubCommand());
    addSubCommand("help", new HelpSubCommand());
    addSubCommand("hide", new HideSubCommand());
    addSubCommand("history", new HistorySubCommand());
    addSubCommand("holdinginfo", new HoldingInfoSubCommand());
    addSubCommand("info", new InfoSubCommand());
    addSubCommand("invite", new InviteSubCommand());
    addSubCommand("invites", new InvitesSubCommand());
    addSubCommand("kickmember", new KickMemberSubCommand());
    addSubCommand("list", new ListSubCommand());
    addSubCommand("listhidden", new ListHiddenSubCommand());
    addSubCommand("setlogo", new LogoSubCommand());
    addSubCommand("memberinfo", new MemberInfoSubCommand());
    addSubCommand("members", new MembersSubCommand());
    addSubCommand("myholdings", new MyHoldingsSubCommand());
    addSubCommand("pay", new PaySubCommand());
    addSubCommand("payuser", new PayUserSubCommand());
    addSubCommand("transfer", new TransferSubCommand());
    addSubCommand("perks", new PerksSubCommand());
    addSubCommand("refresh", new RefreshSubCommand());
    addSubCommand("removeholding", new RemoveHoldingSubCommand());
    addSubCommand("rename", new RenameSubCommand());
    addSubCommand("setrole", new SetRoleSubCommand());
    addSubCommand("baltop", new TopSubCommand());
    addSubCommand("top", new TopSubCommand());
    addSubCommand("unhide", new UnHideSubCommand());
    addSubCommand("unverify", new UnVerifySubCommand());
    addSubCommand("verify", new VerifySubCommand());
    addSubCommand("withdraw", new WithdrawSubCommand());

    addSubCommand("createservice", new CreateServiceSubCommand());
    addSubCommand("paysubscription", new PaySubscriptionSubCommand());
    addSubCommand("serviceinfo", new ServiceInfoSubCommand());
    addSubCommand("servicefolders", new ServiceFoldersSubCommand());
    addSubCommand("services", new ServicesSubCommand());
    addSubCommand("setservicemax", new SetServiceMaxSubCommand());
    addSubCommand("subscribe", new SubscribeSubCommand());
    addSubCommand("subscribers", new SubscribersSubCommand());
    addSubCommand("subscriptions", new SubscriptionsSubCommand());
    addSubCommand("unsubscribe", new UnsubscribeSubCommand());
  }
}
