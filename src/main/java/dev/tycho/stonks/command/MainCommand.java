package dev.tycho.stonks.command;

import dev.tycho.stonks.command.base.CommandBase;
import dev.tycho.stonks.command.subs.FeesCommandSub;
import dev.tycho.stonks.command.subs.HelpCommandSub;
import dev.tycho.stonks.command.subs.ListCommandSub;
import dev.tycho.stonks.command.subs.TopCommandSub;
import dev.tycho.stonks.command.subs.account.*;
import dev.tycho.stonks.command.subs.company.*;
import dev.tycho.stonks.command.subs.holding.CreateHoldingSubCommand;
import dev.tycho.stonks.command.subs.holding.HoldingInfoSubCommand;
import dev.tycho.stonks.command.subs.holding.MyHoldingsCommandSub;
import dev.tycho.stonks.command.subs.holding.RemoveHoldingSubCommand;
import dev.tycho.stonks.command.subs.member.*;
import dev.tycho.stonks.command.subs.moderator.*;
import dev.tycho.stonks.command.subs.service.*;
import dev.tycho.stonks.command.subs.service.subscription.*;

public class MainCommand extends CommandBase {

  public MainCommand() {
    super("command", new ListCommandSub());
    addSubCommand("accounts", new AccountsSubCommand());
    addSubCommand("acceptinvite", new AcceptInviteSubCommand());
    addSubCommand("createaccount", new CreateAccountSubCommand());
    addSubCommand("create", new CreateSubCommand());
    addSubCommand("createholding", new CreateHoldingSubCommand());
    addSubCommand("declineinvite", new DeclineInviteSubCommand());
    addSubCommand("fees", new FeesCommandSub());
    addSubCommand("help", new HelpCommandSub());
    addSubCommand("hide", new HideCommandSub());
    addSubCommand("history", new HistorySubCommand());
    addSubCommand("holdinginfo", new HoldingInfoSubCommand());
    addSubCommand("info", new InfoSubCommand());
    addSubCommand("invite", new InviteSubCommand());
    addSubCommand("invites", new InvitesCommandSub());
    addSubCommand("kickmember", new KickMemberSubCommand());
    addSubCommand("list", new ListCommandSub());
    addSubCommand("listhidden", new ListHiddenCommandSub());
    addSubCommand("setlogo", new LogoCommandSub());
    addSubCommand("memberinfo", new MemberInfoSubCommand());
    addSubCommand("members", new MembersSubCommand());
    addSubCommand("myholdings", new MyHoldingsCommandSub());
    addSubCommand("pay", new PaySubCommand());
    addSubCommand("payuser", new PayUserSubCommand());
    addSubCommand("transfer", new TransferSubCommand());
    addSubCommand("perks", new PerksSubCommand());
    addSubCommand("refresh", new RefreshCommandSub());
    addSubCommand("removeholding", new RemoveHoldingSubCommand());
    addSubCommand("rename", new RenameSubCommand());
    addSubCommand("setrole", new SetRoleSubCommand());
    addSubCommand("baltop", new TopCommandSub());
    addSubCommand("top", new TopCommandSub());
    addSubCommand("unhide", new UnHideCommandSub());
    addSubCommand("unverify", new UnVerifyCommandSub());
    addSubCommand("verify", new VerifyCommandSub());
    addSubCommand("withdraw", new WithdrawSubCommand());

    addSubCommand("createservice", new CreateServiceSubCommand());
    addSubCommand("paysubscription", new PaySubscriptionSubCommand());
    addSubCommand("serviceinfo", new ServiceInfoSubCommand());
    addSubCommand("servicefolders", new ServiceFoldersSubCommand());
    addSubCommand("services", new ServicesSubCommand());
    addSubCommand("setservicemax", new SetServiceMaxSubCommand());
    addSubCommand("subscribe", new SubscribeSubCommand());
    addSubCommand("subscribers", new SubscribersSubCommand());
    addSubCommand("subscriptions", new SubscriptionsCommandSub());
    addSubCommand("unsubscribe", new UnsubscribeSubCommand());
  }
}
