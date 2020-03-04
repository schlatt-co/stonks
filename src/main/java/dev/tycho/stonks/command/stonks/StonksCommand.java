package dev.tycho.stonks.command.stonks;

import dev.tycho.stonks.command.base.CommandBase;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.command.stonks.subs.FeesCommandSub;
import dev.tycho.stonks.command.stonks.subs.HelpCommandSub;
import dev.tycho.stonks.command.stonks.subs.ListCommandSub;
import dev.tycho.stonks.command.stonks.subs.TopCommandSub;
import dev.tycho.stonks.command.stonks.subs.account.*;
import dev.tycho.stonks.command.stonks.subs.company.*;
import dev.tycho.stonks.command.stonks.subs.holding.CreateHoldingCommandSub;
import dev.tycho.stonks.command.stonks.subs.holding.HoldingInfoCommandSub;
import dev.tycho.stonks.command.stonks.subs.holding.MyHoldingsCommandSub;
import dev.tycho.stonks.command.stonks.subs.holding.RemoveHoldingCommandSub;
import dev.tycho.stonks.command.stonks.subs.member.*;
import dev.tycho.stonks.command.stonks.subs.moderator.*;
import dev.tycho.stonks.command.stonks.subs.service.*;
import dev.tycho.stonks.command.stonks.subs.service.subscription.*;

public class StonksCommand extends CommandBase {

  public StonksCommand() {
    super(new ListCommandSub());


    // Company Commands
    addSubCommand("info", new InfoCommandSub());
    addSubCommand("list", new ListCommandSub());
    addSubCommand("members", new MembersCommandSub());
    addSubCommand("perks", new PerksCommandSub());
    addSubCommand("accounts", new AccountsCommandSub());
    addSubCommand("services", new ServicesCommandSub());
    addSubCommand("create", new CreateCommandSub());
    addSubCommand("setlogo", new LogoCommandSub());

    // Membership Commands
    addSubCommand("acceptinvite", new AcceptInviteCommandSub());
    addSubCommand("declineinvite", new DeclineInviteCommandSub());
    addSubCommand("invite", new InviteCommandSub());
    addSubCommand("invites", new InvitesCommandSub());
    addSubCommand("setrole", new SetRoleCommandSub());
    addSubCommand("kickmember", new KickMemberCommandSub());
    addSubCommand("memberinfo", new MemberInfoCommandSub());

    // Account Commands
    addSubCommand("createaccount", new CreateAccountCommandSub());
    addSubCommand("history", new HistoryCommandSub());

    // Holding Commands
    addSubCommand("createholding", new CreateHoldingCommandSub());
    addSubCommand("removeholding", new RemoveHoldingCommandSub());
    addSubCommand("holdinginfo", new HoldingInfoCommandSub());
    addSubCommand("myholdings", new MyHoldingsCommandSub());

    // Payment Commands
    addSubCommand("pay", new PayCommandSub());
    addSubCommand("withdraw", new WithdrawCommandSub());
    addSubCommand("payuser", new PayUserCommandSub());
    addSubCommand("transfer", new TransferCommandSub());

    // Service Commands
    addSubCommand("createservice", new CreateServiceCommandSub());
    addSubCommand("serviceinfo", new ServiceInfoCommandSub());
    addSubCommand("servicefolders", new ServiceFoldersCommandSub());
    addSubCommand("setservicemax", new SetServiceMaxCommandSub());
    addSubCommand("subscribers", new SubscribersCommandSub());

    // Subscription Commands
    addSubCommand("subscribe", new SubscribeCommandSub());
    addSubCommand("unsubscribe", new UnsubscribeCommandSub());
    addSubCommand("paysubscription", new PaySubscriptionCommandSub());
    addSubCommand("subscriptions", new SubscriptionsCommandSub());

    // Admin commands
    addSubCommand("hide", CommandSub.perms("trevor.mod", new HideCommandSub()));
    addSubCommand("unhide", CommandSub.perms("trevor.mod", new UnHideCommandSub()));
    addSubCommand("verify", CommandSub.perms("trevor.mod", new VerifyCommandSub()));
    addSubCommand("unverify", CommandSub.perms("trevor.mod", new UnVerifyCommandSub()));
    addSubCommand("rename", CommandSub.perms("trevor.mod", new RenameCommandSub()));
    addSubCommand("listhidden", CommandSub.perms("trevor.mod", new ListHiddenCommandSub()));
    addSubCommand("refresh", CommandSub.perms("trevor.admin", new RefreshCommandSub()));

    // Misc Commands

    addSubCommand("help", new HelpCommandSub());
    addSubCommand("baltop", new TopCommandSub());
    addSubCommand("top", new TopCommandSub());
    addSubCommand("fees", new FeesCommandSub());

  }
}
