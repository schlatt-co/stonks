package dev.tycho.stonks.command.stonks;

import dev.tycho.stonks.command.base.CommandBase;
import dev.tycho.stonks.command.base.SimpleCommandSub;
import dev.tycho.stonks.command.stonks.subs.FeesCommandSub;
import dev.tycho.stonks.command.stonks.subs.ListCommandSub;
import dev.tycho.stonks.command.stonks.subs.TopCommandSub;
import dev.tycho.stonks.command.stonks.subs.account.*;
import dev.tycho.stonks.command.stonks.subs.company.*;
import dev.tycho.stonks.command.stonks.subs.holding.*;
import dev.tycho.stonks.command.stonks.subs.member.*;
import dev.tycho.stonks.command.stonks.subs.moderator.*;
import dev.tycho.stonks.command.stonks.subs.service.*;
import dev.tycho.stonks.command.stonks.subs.service.subscription.*;
import org.bukkit.entity.Player;

public class StonksCommand extends CommandBase {

  public StonksCommand() {
    super("stonks", new ListCommandSub());


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
    addSubCommand("removeholdinguuid", new RemoveHoldingUUIDCommandSub());
    addSubCommand("holdinginfo", new HoldingInfoCommandSub());
    addSubCommand("myholdings", new MyHoldingsCommandSub());

    // Payment Commands
    addSubCommand("pay", new PayCommandSub());
    addSubCommand("payaccount", new PayAccountCommandSub());
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
    addSubCommand("hide", new HideCommandSub());
    addSubCommand("unhide", new UnHideCommandSub());
    addSubCommand("verify", new VerifyCommandSub());
    addSubCommand("unverify", new UnVerifyCommandSub());
    addSubCommand("rename", new RenameCommandSub());
    addSubCommand("listhidden", new ListHiddenCommandSub());
    addSubCommand("delete", new DeleteCommandSub());
    addSubCommand("refresh", new RefreshCommandSub());

    // Misc Commands

    addSubCommand("help", new SimpleCommandSub() {
      @Override
      public void execute(Player player) {
        sendMessage(player, "Command Help:");
        sendMessage(player, "To view all commands and more info about the plugin please go to https://stonks.company/");
      }
    });
    addSubCommand("baltop", new TopCommandSub());
    addSubCommand("top", new TopCommandSub());
    addSubCommand("fees", new FeesCommandSub());

  }
}
