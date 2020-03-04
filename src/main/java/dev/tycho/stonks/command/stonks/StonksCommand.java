package dev.tycho.stonks.command.stonks;

import dev.tycho.stonks.command.base.CommandBase;
import dev.tycho.stonks.command.base.LambdaSubCommand;
import dev.tycho.stonks.command.base.SubCommand;
import dev.tycho.stonks.command.stonks.subs.FeesSubCommand;
import dev.tycho.stonks.command.stonks.subs.ListSubCommand;
import dev.tycho.stonks.command.stonks.subs.TopSubCommand;
import dev.tycho.stonks.command.stonks.subs.account.*;
import dev.tycho.stonks.command.stonks.subs.company.*;
import dev.tycho.stonks.command.stonks.subs.holding.CreateHoldingSubCommand;
import dev.tycho.stonks.command.stonks.subs.holding.HoldingInfoSubCommand;
import dev.tycho.stonks.command.stonks.subs.holding.RemoveHoldingSubCommand;
import dev.tycho.stonks.command.stonks.subs.member.*;
import dev.tycho.stonks.command.stonks.subs.moderator.*;
import dev.tycho.stonks.command.stonks.subs.service.*;
import dev.tycho.stonks.command.stonks.subs.service.subscription.*;
import dev.tycho.stonks.gui.AllPlayerHoldingsGui;

public class StonksCommand extends CommandBase {

  public StonksCommand() {
    super(new ListSubCommand());


    // Company Commands
    addSubCommand("info", new InfoSubCommand());
    addSubCommand("list", new ListSubCommand());
    addSubCommand("members", new MembersSubCommand());
    addSubCommand("perks", new PerksSubCommand());
    addSubCommand("accounts", new AccountsSubCommand());
    addSubCommand("services", new ServicesSubCommand());
    addSubCommand("create", new CreateSubCommand());
    addSubCommand("setlogo", new LogoSubCommand());

    // Membership Commands
    addSubCommand("acceptinvite", new AcceptInviteSubCommand());
    addSubCommand("declineinvite", new DeclineInviteSubCommand());
    addSubCommand("invite", new InviteSubCommand());
    addSubCommand("invites", new InvitesSubCommand());
    addSubCommand("setrole", new SetRoleSubCommand());
    addSubCommand("kickmember", new KickMemberSubCommand());
    addSubCommand("memberinfo", new MemberInfoSubCommand());

    // Account Commands
    addSubCommand("createaccount", new CreateAccountSubCommand());
    addSubCommand("history", new HistorySubCommand());

    // Holding Commands
    addSubCommand("createholding", new CreateHoldingSubCommand());
    addSubCommand("removeholding", new RemoveHoldingSubCommand());
    addSubCommand("holdinginfo", new HoldingInfoSubCommand());
    addSubCommand("myholdings", new LambdaSubCommand((p) -> new AllPlayerHoldingsGui(p).show(p)));

    // Payment Commands
    addSubCommand("pay", new PaySubCommand());
    addSubCommand("withdraw", new WithdrawSubCommand());
    addSubCommand("payuser", new PayUserSubCommand());
    addSubCommand("transfer", new TransferSubCommand());

    // Service Commands
    addSubCommand("createservice", new CreateServiceSubCommand());
    addSubCommand("serviceinfo", new ServiceInfoSubCommand());
    addSubCommand("servicefolders", new ServiceFoldersSubCommand());
    addSubCommand("setservicemax", new SetServiceMaxSubCommand());
    addSubCommand("subscribers", new SubscribersSubCommand());

    // Subscription Commands
    addSubCommand("subscribe", new SubscribeSubCommand());
    addSubCommand("unsubscribe", new UnsubscribeSubCommand());
    addSubCommand("paysubscription", new PaySubscriptionSubCommand());
    addSubCommand("subscriptions", new SubscriptionsSubCommand());

    // Admin commands
    addSubCommand("hide", SubCommand.perms("trevor.mod", new HideSubCommand()));
    addSubCommand("unhide", SubCommand.perms("trevor.mod", new UnHideSubCommand()));
    addSubCommand("verify", SubCommand.perms("trevor.mod", new VerifySubCommand()));
    addSubCommand("unverify", SubCommand.perms("trevor.mod", new UnVerifySubCommand()));
    addSubCommand("rename", SubCommand.perms("trevor.mod", new RenameSubCommand()));
    addSubCommand("listhidden", SubCommand.perms("trevor.mod", new ListHiddenSubCommand()));
    addSubCommand("refresh", SubCommand.perms("trevor.admin", new RefreshSubCommand()));

    // Misc Commands

    addSubCommand("help", new LambdaSubCommand((p) -> {
      sendMessage(p, "Command Help:");
      sendMessage(p, "To view all commands and more info about the plugin please go to https://stonks.company/");
    }));
    addSubCommand("baltop", new TopSubCommand());
    addSubCommand("top", new TopSubCommand());
    addSubCommand("fees", new FeesSubCommand());

  }
}
