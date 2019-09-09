package dev.tycho.stonks.command;

import dev.tycho.stonks.command.base.CommandBase;
import dev.tycho.stonks.command.company.*;

public class MainCommand extends CommandBase {

  public MainCommand() {
    super("command", new ListCommandSub());
    addSubCommand("accounts", new AccountsCommandSub());
    addSubCommand("createaccount", new CreateAccountCommandSub());
    addSubCommand("create", new CreateCommandSub());
    addSubCommand("createholding", new CreateHoldingCommandSub());
    addSubCommand("fees", new FeesCommandSub());
    addSubCommand("help", new HelpCommandSub());
    addSubCommand("hide", new HideCommandSub());
    addSubCommand("history", new HistoryCommandSub());
    addSubCommand("holdinginfo", new HoldingInfoCommandSub());
    addSubCommand("info", new InfoCommandSub());
    addSubCommand("invite", new InviteCommandSub());
    addSubCommand("invites", new InvitesCommandSub());
    addSubCommand("kickmember", new KickMemberCommandSub());
    addSubCommand("list", new ListCommandSub());
    addSubCommand("listhidden", new ListHiddenCommandSub());
    addSubCommand("setlogo", new LogoCommandSub());
    addSubCommand("memberinfo", new MemberInfoCommandSub());
    addSubCommand("members", new MembersCommandSub());
    addSubCommand("pay", new PayCommandSub());
    addSubCommand("removeholding", new RemoveHoldingCommandSub());
    addSubCommand("rename", new RenameCommandSub());
    addSubCommand("setrole", new SetRoleCommandSub());
    addSubCommand("baltop", new TopCommandSub());
    addSubCommand("top", new TopCommandSub());
    addSubCommand("unhide", new UnHideCommandSub());
    addSubCommand("unverify", new UnVerifyCommandSub());
    addSubCommand("verify", new VerifyCommandSub());
    addSubCommand("withdraw", new WithdrawCommandSub());

    addSubCommand("createservice", new CreateServiceCommandSub());
    addSubCommand("paysubscription", new PaySubscriptionCommandSub());
    addSubCommand("serviceinfo", new ServiceInfoCommandSub());
    addSubCommand("servicefolders", new ServiceFoldersCommandSub());
    addSubCommand("services", new ServicesCommandSub());
    addSubCommand("setservicemax", new SetServiceMaxCommandSub());
    addSubCommand("subscribe", new SubscribeCommandSub());
    addSubCommand("subscribers", new SubscribersCommandSub());
    addSubCommand("subscriptions", new SubscriptionsCommandSub());
    addSubCommand("unsubscribe", new UnsubscribeCommandSub());
  }
}
