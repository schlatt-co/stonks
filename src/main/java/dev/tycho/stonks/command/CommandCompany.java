package dev.tycho.stonks.command;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.gui.*;
import dev.tycho.stonks.logging.Transaction;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.managers.GuiManager;
import dev.tycho.stonks.managers.MessageManager;
import dev.tycho.stonks.model.*;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static dev.tycho.stonks.model.Role.*;

//TODO break this class up into sublcasses
// its almost 1000 lines long
public class CommandCompany implements CommandExecutor {

    private DatabaseManager databaseManager;
    private GuiManager guiManager;
    private JavaPlugin plugin;
    private Essentials ess;

    public CommandCompany(DatabaseManager databaseManager, Stonks plugin) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;
        this.ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
        this.guiManager = (GuiManager) plugin.getModule("guiManager");
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            //add list with all commands here later;
            MessageManager.sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create": {
                if (args.length > 1) {
                    companyCreate(args[1], player);
                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks create <company>");
                }
                return true;
            }
            case "invites": {
                openInvitesList(player);
                return true;
            }
            case "list": {
                openCompanyList(player, OrderBy.NAMEASC);
                return true;
            }
            case "info": {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please specify a company!");
                    return true;
                }
                openCompanyInfo(player, args[1]);
                return true;
            }
            case "members": {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please specify a company!");
                    return true;
                }
                openCompanyMembers(player, args[1]);
                return true;
            }
            case "accounts": {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please specify a company!");
                    return true;
                }
                openCompanyAccounts(player, args[1]);
                return true;
            }
            case "invite": {
                if (args.length > 2) {
                    return invitePlayerToCompany(args[1], args[2], player);
                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks invite <player> <company>");
                    return true;
                }
            }
            // /comp createcompanyaccount <company_name> <account_name>
            case "createcompanyaccount": {
                if (args.length > 2) {
                    createCompanyAccount(player, args[1], args[2]);
                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks createcompanyaccount <company_name> <account_name>");
                }
                return true;
            }
            // /comp createholdingsaccount <company_name> <account_name>
            case "createholdingsaccount": {
                if (args.length > 2) {
                    createHoldingsAccount(player, args[1], args[2]);
                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks createholdingsaccount <company_name> <account_name>");
                }
                return true;
            }
            // /comp createholding <account_id> <player_name> <share>
            case "createholding": {
                if (args.length > 3) {
                    createHolding(player, Integer.parseInt(args[1]), args[2], Double.parseDouble(args[3]));
                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks createholding <account_id> <player_name> <share>");
                }
                return true;
            }
            // /comp removeholding <accountid> <player_name>
            case "removeholding": {
                if (args.length > 2) {
                    removeHolding(player, Integer.parseInt(args[1]), args[2]);
                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks removeholding <accountid> <player_name>");
                }
                return true;
            }
            // /comp withdraw <amount> <accountid>
            case "withdraw": {
                if (args.length > 2) {
                    withdrawFromAccount(player, Double.parseDouble(args[1]), Integer.parseInt(args[2]));
                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks withdraw <amount> <accountid>");
                }
                return true;
            }
            case "setlogo": {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please specify a company!");
                    return true;
                }
                setLogo(player, args[1]);
                return true;
            }
            case "pay": {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks pay <amount> <accountid>");
                    return true;
                }
                payAccount(Double.parseDouble(args[1]), Integer.parseInt(args[2]), player);
                return true;
            }
            // /comp setrole <playername> <company> <role>
            case "setrole": {
                if (args.length > 3) {
                    setRole(player, args[1], args[2], args[3]);
                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks setrole <player> <company> <role>");
                }
                return true;
            }
            case "memberinfo": {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /stonks memberinfo <player> <company>");
                    return true;
                }
                openMemberInfo(args[1], args[2], player);
                return true;
            }
            case "kickmember": {
                if (args.length < 3) {
                    player.performCommand(ChatColor.RED + "Correct usage: /stonks kickmember <player> <company>");
                    return true;
                }
                kickMember(args[1], args[2], player);
                return true;
            }
        }
        MessageManager.sendHelpMessage(player);
        return true;
    }

    private void removeHolding(Player player, int accountId, String playerName) {
        try {
            AccountLink link = databaseManager.getAccountLinkDao().queryForId(accountId);
            if (link != null) {
                //We have a valid account
                //First make sure the account is a holdings account
                //todo turn this into a visitor, try and avoid casts
                if (link.getAccountType() == AccountType.HoldingsAccount) {
                    HoldingsAccount account = (HoldingsAccount) link.getAccount();
                    Member member = link.getCompany().getMember(player);
                    //Is the player a member of that company
                    if (member != null) {
                        //Does the player have permission to create a holding in that account?
                        if (member.hasManagamentPermission()) {
                            //Try and find the UUID of that player
                            User u = ess.getOfflineUser(playerName);
                            //check if the player has been on the server
                            if (u != null) {
                                Player op = ess.getOfflineUser(playerName).getBase();
                                Holding playerHolding = account.getPlayerHolding(op.getUniqueId());
                                if (playerHolding != null) {
                                    //That player has a holding
                                    //If their balance is lower than 1 we can remove it
                                    //This isnt == 0 because of possible floating point errors
                                    if (playerHolding.getBalance() < 1) {
                                        account.removeHolding(playerHolding);
                                        databaseManager.getHoldingDao().delete(playerHolding);
                                        player.sendMessage(ChatColor.GREEN + "Holding successfully removed!");
                                    } else {
                                        player.sendMessage(ChatColor.RED + "There is more than $1 in that holding");
                                        player.sendMessage(ChatColor.RED + "Please get " + playerName +
                                                " to withdraw so there is less than $1 remaining");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "There is no holding for the player " + playerName);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Player " + playerName + " not found");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have the correct permissions within your company to remove a holding");
                            player.sendMessage("Ask your manager to promote you to a manager to do this");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not a member of that company");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The account ID entered is not a holdings account");
                }
            } else {
                player.sendMessage(ChatColor.RED + "No account exists for that ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "SQL ERROR please tell wheezy this happened");
        }
    }

    private void setRole(Player player, String playerName, String companyName, String roleString) {
        Stonks.newChain()
                .async(() -> {
                    //Try and parse the role
                    Role newRole;
                    try {
                        newRole = Role.valueOf(roleString);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(ChatColor.RED + "Role entered was not a valid role");
                        return;
                    }
                    Bukkit.broadcastMessage(newRole.toString());

                    //Now see if the player to promote exists
                    Player playerToChange = ess.getUser(playerName).getBase();
                    if (playerToChange != null) {
                        Company company = null;
                        try {
                            company = databaseManager.getCompanyDao().getCompany(companyName);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            player.sendMessage(ChatColor.RED + "SQL error tell wheezy");
                            return;
                        }
                        //Find the company they are making the changes in
                        if (company != null) {
                            //Now check both are members
                            Member changingMember = company.getMember(player);
                            if (changingMember != null) {
                                Member memberToChange = company.getMember(playerToChange);
                                if (memberToChange != null) {
                                    //Both players are a member of the company
                                    //Now check permissions
                                    //A player can't change their own role
                                    if (!changingMember.getUuid().equals(memberToChange.getUuid())) {
                                        if (changingMember.canChangeRole(memberToChange, newRole)) {
                                            //If we are promoting them to a ceo then demote us
                                            try {
                                                databaseManager.getMemberDao().setRole(memberToChange, newRole);
                                                player.sendMessage(ChatColor.GREEN + "Success! " + playerName + " now has role " + roleString);
                                                if (newRole == CEO) {
                                                    databaseManager.getMemberDao().setRole(changingMember, Manager);
                                                    player.sendMessage(ChatColor.GREEN + "You promoted " + playerName +
                                                            " to CEO, you have been demoted to a Manager since there can only be one CEO.");
                                                }
                                            } catch (SQLException e) {
                                                player.sendMessage(ChatColor.RED + "SQL ERROR! Tell wheezy please");
                                                e.printStackTrace();
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "You do not have the permissions to promote " + playerName + " to " + roleString);
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "You cannot change your own role");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "The player you are changing the role of is not a member of that company");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You are not a member of that company");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Company does not exist");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "The user you are trying to change does not exist");
                    }
                }).execute();
    }

    private void createHolding(Player player, int accountId, String playerName, double share) {
        Stonks.newChain()
                .async(() -> {
                    if (share <= 0) {
                        player.sendMessage(ChatColor.RED + "Holding share must be greater than 0");
                        return;
                    }
                    try {
                        AccountLink link = databaseManager.getAccountLinkDao().queryForId(accountId);
                        if (link != null) {
                            //We have a valid account
                            Member member = link.getCompany().getMember(player);
                            //First make sure the account is a holdings account
                            //todo turn this into a visitor, try and avoid casts
                            if (link.getAccountType() == AccountType.HoldingsAccount) {
                                HoldingsAccount account = (HoldingsAccount) link.getAccount();
                                //Is the player a member of that company
                                if (member != null) {
                                    //Does the player have permission to create a holding in that account?
                                    if (member.hasManagamentPermission()) {
                                        //Try and find the UUID of that player
                                        User u = ess.getOfflineUser(playerName);
                                        //check if the player has been on the server
                                        if (u != null) {
                                            Player op = ess.getOfflineUser(playerName).getBase();
                                            if (account.getPlayerHolding(op.getUniqueId()) == null) {
                                                //We can make a holding
                                                Holding holding = new Holding(op.getUniqueId(), share, account);
                                                databaseManager.getHoldingDao().create(holding);
                                                player.sendMessage(ChatColor.GREEN + "Holding successfully created!");
                                            } else {
                                                player.sendMessage(ChatColor.RED + "The player you are making a holding for already has a holding in this account");
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "The player you are making a holding for has never played on this server");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "You don't have the correct permissions within your company to create a holding");
                                        player.sendMessage("Ask your manager to promote you to a manager to do this");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You are not a member of that company");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The account ID entered is not a holdings account");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "No account exists for that ID");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "SQL ERROR please tell wheezy this happened");
                    }
                }).execute();
    }

    private void withdrawFromAccount(Player player, double amount, int accountId) {
        Stonks.newChain()
                .async(() -> {
                    try {
                        AccountLink link = databaseManager.getAccountLinkDao().queryForId(accountId);
                        if (link != null) {
                            //We have a valid account
                            //First check they are a member of the company
                            if (link.getCompany().hasMember(player)) {
                                Member member = link.getCompany().getMember(player);
                                IAccountVisitor visitor = new IAccountVisitor() {
                                    @Override
                                    public void visit(CompanyAccount a) {
                                        //With a company account we need to verify they have withdraw permission
                                        if (member.hasManagamentPermission()) {
                                            if (a.getTotalBalance() >= amount) {
                                                a.subtractBalance(amount);
                                                try {
                                                    databaseManager.getCompanyAccountDao().update(a);
                                                    Stonks.economy.depositPlayer(player, amount);
                                                    //todo transaction fee
                                                    player.sendMessage(ChatColor.GREEN + "Money Withdrawn!");


                                                    //Log the transaction
                                                    databaseManager.logTransaction(new Transaction(link, player.getUniqueId(), -amount));

                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                    player.sendMessage(ChatColor.RED + "SQL ERROR please tell wheezy this happened");
                                                }

                                            } else {
                                                player.sendMessage(ChatColor.RED + "There is not enough money in that account");
                                                player.sendMessage(ChatColor.RED + "make some more you poor fuck");
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "You don't have the correct permissions within your company to withdraw from that account");
                                            player.sendMessage("Ask your manager to promote you to withdraw from this account");
                                        }
                                    }

                                    @Override
                                    public void visit(HoldingsAccount a) {
                                        //Check to see if they own a holding in this holdingsaccount
                                        Holding h = a.getPlayerHolding(player.getUniqueId());
                                        if (h != null) {
                                            //They have a holding
                                            if (h.getBalance() >= amount) {
                                                //They have enough money so we can withdraw
                                                try {
                                                    h.subtractBalance(amount);
                                                    databaseManager.getHoldingDao().update(h);
                                                    Stonks.economy.depositPlayer(player, amount);
                                                    //todo transaction fee
                                                    player.sendMessage(ChatColor.GREEN + "Money Withdrawn!");
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                    player.sendMessage(ChatColor.RED + "SQL ERROR please tell wheezy this happened");
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.RED + "There is not enough money in your holding");
                                                player.sendMessage(ChatColor.RED + "earn some more, scrounger");
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "You don't have a holding in this account");
                                        }
                                    }
                                };
                                link.getAccount().accept(visitor);
                            } else {
                                player.sendMessage(ChatColor.RED + "You are not a member of the company this account belongs to");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "No account with this ID exists");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "SQL ERROR please tell wheezy this happened");
                    }
                }).execute();
    }

    private void createCompanyAccount(Player player, String companyName, String accountName) {
        Stonks.newChain()
                .async(() -> {
                    try {
                        Company company = databaseManager.getCompanyDao().getCompany(companyName);
                        if (company != null) {
                            //dont allow duplicate account names
                            //todo this doesn't work
                            if (company.getAccounts().stream().noneMatch(
                                    a -> a.getAccount().getName().toLowerCase().equals(accountName.toLowerCase()))) {
                                Member member = company.getMember(player);
                                if (member != null) {
                                    if (member.hasManagamentPermission()) {
                                        CompanyAccount ca = new CompanyAccount(accountName);
                                        databaseManager.getCompanyAccountDao().create(ca);

                                        AccountLink link = new AccountLink(company, ca);
                                        databaseManager.getAccountLinkDao().create(link);

                                        player.sendMessage(ChatColor.GREEN + "Company account '" + accountName + "' added to '" + companyName + "' !");
                                    } else {
                                        player.sendMessage(ChatColor.RED + "You don't have the correct permissions within your company to create an account");
                                        player.sendMessage("Ask your manager to promote you to a manager to do this");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You are not a member of this company");
                                }

                            } else {
                                player.sendMessage(ChatColor.RED + "Company already has account with that name");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Company name invalid!");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "SQL ERROR please tell wheezy this happened");
                    }
                }).execute();
    }

    private void createHoldingsAccount(Player player, String companyName, String accountName) {
        Stonks.newChain()
                .async(() -> {
                    try {
                        Company company = databaseManager.getCompanyDao().getCompany(companyName);
                        if (company != null) {
                            //dont allow duplicate account names
                            //todo this doesn't work
                            if (company.getAccounts().stream().noneMatch(
                                    a -> a.getAccount().getName().toLowerCase().equals(accountName.toLowerCase()))) {
                                Member member = company.getMember(player);
                                if (member != null) {
                                    if (member.hasManagamentPermission()) {
                                        HoldingsAccount ha = new HoldingsAccount(accountName);
                                        databaseManager.getHoldingAccountDao().create(ha);

                                        AccountLink link = new AccountLink(company, ha);
                                        databaseManager.getAccountLinkDao().create(link);

                                        player.sendMessage(ChatColor.GREEN + "Holdings account '" + accountName + "' added to '" + companyName + "' !");
                                    } else {
                                        player.sendMessage(ChatColor.RED + "You don't have the correct permissions within your company to create an account");
                                        player.sendMessage("Ask your manager to promote you to a manager to do this");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You are not a member of this company");
                                }

                            } else {
                                player.sendMessage(ChatColor.RED + "Company already has account with that name");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Company name invalid!");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "SQL ERROR please tell wheezy this happened");
                    }
                }).execute();
    }

    private void kickMember(String memberName, String companyName, Player player) {
        Stonks.newChain()
                .async(() -> {
                    try {
                        User u = ess.getUser(memberName);
                        if (u == null) {
                            player.sendMessage(ChatColor.RED + "That player could not be found!");
                            return;
                        }
                        Player playerProfile = u.getBase();
                        Company company = databaseManager.getCompanyDao().getCompany(companyName);

                        if (company == null) {
                            player.sendMessage(ChatColor.RED + "That player/company could not be found!");
                            return;
                        }

                        Member memberToKick = databaseManager.getMemberDao().getMember(playerProfile, company);
                        if (memberToKick == null) {
                            player.sendMessage(ChatColor.RED + "That player isn't a member of that company!");
                            return;
                        }

                        Member sender = databaseManager.getMemberDao().getMember(player, company);
                        if (sender == null || !sender.hasManagamentPermission()) {
                            player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                            return;
                        }
                        if (memberToKick.getRole() == Role.CEO) {
                            player.sendMessage(ChatColor.RED + "You can't kick a CEO!");
                            return;
                        }

                        if (memberToKick.hasHoldings(databaseManager)) {
                            player.sendMessage(ChatColor.RED + "This player still has holdings, delete them before kicking the player!");
                            return;
                        }
                        databaseManager.getMemberDao().deleteMember(memberToKick);
                        player.sendMessage(ChatColor.GREEN + "Member has been kicked successfully");
                        return;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                })
                .sync(() -> {
                    player.performCommand("stonks members " + companyName);
                })
                .execute();
    }

    private void openMemberInfo(String memberName, String companyName, Player player) {
        Stonks.newChain()
                .asyncFirst(() -> {
                    try {
                        Player playerProfile = ess.getUser(memberName).getBase();
                        Company company = databaseManager.getCompanyDao().getCompany(companyName);

                        if (company == null || playerProfile == null) {
                            player.sendMessage(ChatColor.RED + "That player/company could not be found!");
                            return null;
                        }

                        Member member = databaseManager.getMemberDao().getMember(playerProfile, company);
                        if (member == null) {
                            player.sendMessage(ChatColor.RED + "That player isn't a member of that company!");
                            return null;
                        }
                        return MemberInfoGui.getInventory(member);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .abortIfNull()
                .sync((result) -> result.open(player))
                .execute();
    }

    private void payAccount(double amount, int accountId, Player sender) {
        Stonks.newChain()
                .async(() -> {
                    AccountLink accountLink = null;
                    try {
                        accountLink = databaseManager.getAccountLinkDao().queryForId(accountId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (accountLink == null) {
                        sender.sendMessage(ChatColor.RED + "That account doesn't exist!");
                        return;
                    }

                    if (!Stonks.economy.withdrawPlayer(sender, amount).transactionSuccess()) {
                        sender.sendMessage(ChatColor.RED + "Insufficient funds!");
                        return;
                    }

                    accountLink.getAccount().addBalance(amount);
                    IAccountVisitor visitor = new IAccountVisitor() {
                        @Override
                        public void visit(CompanyAccount a) {
                            try {
                                databaseManager.getCompanyAccountDao().update(a);
                            } catch (SQLException e) {
                                sender.sendMessage(ChatColor.RED + "SQL ERRROR PAYING TELL WHEEZYYYYYY PLEASE ");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void visit(HoldingsAccount a) {
                            try {
                                databaseManager.getHoldingAccountDao().update(a);
                                for (Holding h : a.getHoldings()) {
                                    databaseManager.getHoldingDao().update(h);
                                }
                            } catch (SQLException e) {
                                sender.sendMessage(ChatColor.RED + "SQL ERRROR PAYING TELL WHEEZYYYYYY PLEASE ");
                                e.printStackTrace();
                            }
                        }
                    };
                    accountLink.getAccount().accept(visitor);
                    //Log the transaction
                    databaseManager.logTransaction(new Transaction(accountLink, sender.getUniqueId(), amount));
                    //Tell the user we paid the account
                    sender.sendMessage(ChatColor.GREEN + "Paid " + ChatColor.DARK_GREEN + accountLink.getCompany().getName() +
                            " (" + accountLink.getAccount().getName() + ")" + ChatColor.GREEN + " $" + amount + "!");

                }).execute();
    }

    private void setLogo(Player player, String companyName) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() == 0) {
            player.sendMessage(ChatColor.RED + "You must be holding an item to set it as your company logo!");
            return;
        }
        Stonks.newChain()
                .async(() -> {
                    Company company = null;
                    try {
                        company = databaseManager.getCompanyDao().getCompany(companyName);
                        if (company == null) {
                            player.sendMessage(ChatColor.RED + "That company does not exist!");
                            return;
                        }
                        if (!company.hasMember(player)) {
                            player.sendMessage(ChatColor.RED + "You're not a member of that company!");
                            return;
                        }
                        company.setLogoMaterial(itemInHand.getType().name());
                        databaseManager.getCompanyDao().update(company);
                        player.sendMessage(ChatColor.GREEN + "Company logo updated successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                })
                .execute();
    }

    private void companyCreate(String companyName, Player player) {
        Stonks.newChain()
                .async(() -> {
                    if (companyName.length() > 32) {
                        player.sendMessage(ChatColor.RED + "A company name can't be longer than 32 characters!");
                        return;
                    }
                    try {
                        if (databaseManager.getCompanyDao().companyExists(companyName)) {
                            player.sendMessage(ChatColor.RED + "A company with that name already exists!");
                            return;
                        }
                        double creationFee = plugin.getConfig().getInt("fees.companycreation");
                        if (!Stonks.economy.withdrawPlayer(player, creationFee).transactionSuccess()) {
                            player.sendMessage(ChatColor.RED + "There is a $" + creationFee + " fee for creating a company and you did not have sufficient funds, get more money you poor fuck.");
                            return;
                        }
                        Company newCompany = new Company(companyName, "S" + companyName, player);
                        databaseManager.getCompanyDao().assignEmptyForeignCollection(newCompany, "members");
                        databaseManager.getCompanyDao().create(newCompany);

                        CompanyAccount companyAccount = new CompanyAccount("Main");
                        databaseManager.getCompanyAccountDao().create(companyAccount);

                        //Create an link so the account is stored as belonging to the new company
                        AccountLink link = new AccountLink(newCompany, companyAccount);
                        databaseManager.getAccountLinkDao().create(link);

                        Member creator = new Member(player, CEO);
                        newCompany.getMembers().add(creator);

                        player.sendMessage(ChatColor.GREEN + "Company with name: \"" + companyName + "\" created successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "Something went wrong! :(");
                    }
                }).execute();
    }

    private boolean invitePlayerToCompany(String playerToInvite, String companyName, Player player) {
        Stonks.newChain()
                .async(() -> {
                    try {
                        User u = ess.getOfflineUser(playerToInvite);
                        if (u == null) {
                            player.sendMessage(ChatColor.RED + "That player has never played on this server");
                            return;
                        }
                        Player playerToInviteObject = u.getBase();

                        QueryBuilder<Company, UUID> queryBuilder = databaseManager.getCompanyDao().queryBuilder();
                        queryBuilder.where().eq("name", companyName);
                        List<Company> companies = queryBuilder.query();
                        if (companies.isEmpty()) {
                            player.sendMessage(ChatColor.RED + "That company does not exist.");
                            return;
                        }
                        if (companies.get(0).getMember(player) == null) {
                            player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                            return;
                        }
                        if (!companies.get(0).getMember(player).hasManagamentPermission()) {
                            player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                            return;
                        }
                        Member newMember = new Member(playerToInviteObject, Employee, companies.get(0));
                        QueryBuilder<Member, UUID> checkQueryBuilder = databaseManager.getMemberDao().queryBuilder();
                        checkQueryBuilder.where().eq("uuid", newMember.getUuid()).and().eq("company_id", newMember.getCompany().getId());
                        List<Member> list = checkQueryBuilder.query();
                        if (!list.isEmpty()) {
                            if (list.get(0).getAcceptedInvite()) {
                                player.sendMessage(ChatColor.RED + playerToInvite + " is already a member of " + newMember.getCompany().getName());
                            } else {
                                player.sendMessage(ChatColor.RED + playerToInvite + " has already been invited to " + newMember.getCompany().getName());
                            }
                            return;
                        }
                        databaseManager.getMemberDao().create(newMember);
                        player.sendMessage(playerToInviteObject.getName() + " has successfully been invited!");
                        playerToInviteObject.sendMessage("You have been invited to join " + companyName);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }).execute();
        return true;
    }

    private void openCompanyList(Player player, OrderBy orderBy) {
        player.sendMessage(ChatColor.AQUA + "Fetching company list, one moment...");
        Stonks.newChain()
                .asyncFirst(() -> {
                    List<Company> list = null;
                    try {
                        QueryBuilder<Company, UUID> companyQueryBuilder = databaseManager.getCompanyDao().queryBuilder();
                        switch (orderBy) {
                            case NAMEASC: {
                                companyQueryBuilder.orderBy("name", true);
                                break;
                            }
                            case NAMEDESC: {
                                companyQueryBuilder.orderBy("name", false);
                                break;
                            }
//                          case COMPANYVALUE: {
//                              QueryBuilder<CompanyAccount, Integer> accountQueryBuilder = databaseManager.getCompanyAccountDao().queryBuilder();
//                              companyQueryBuilder.leftJoin(accountQueryBuilder);
//                              companyQueryBuilder.orderBy("companyaccount.balance", false);
//                          }
                        }
                        list = companyQueryBuilder.query();
                        for (Company company : list) {
                            company.calculateTotalValue();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return CompanyListGui.getInventory(list);
                })
                .sync((result) -> result.open(player))
                .execute();
    }

    private enum OrderBy {
        NAMEASC, NAMEDESC, COMPANYVALUE
    }

    private void openCompanyInfo(Player player, String companyName) {
        Stonks.newChain()
                .asyncFirst(() -> {
                    try {
                        Company company = databaseManager.getCompanyDao().getCompany(companyName);
                        if (company == null) {
                            player.sendMessage(ChatColor.RED + "That company doesn't exist!");
                            return null;
                        }
                        return CompanyInfoGui.getInventory(company);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .abortIfNull()
                .sync((result) -> result.open(player))
                .execute();
    }

    private void openCompanyMembers(Player player, String companyName) {
        Stonks.newChain()
                .asyncFirst(() -> {
                    try {
                        Company company = databaseManager.getCompanyDao().getCompany(companyName);
                        if (company == null) {
                            player.sendMessage(ChatColor.RED + "That company doesn't exist!");
                            return null;
                        }
                        List<Member> list = null;
                        try {
                            QueryBuilder<Member, UUID> queryBuilder = databaseManager.getMemberDao().queryBuilder();
                            queryBuilder.where().eq("company_id", company.getId()).and().eq("acceptedInvite", true);
                            list = queryBuilder.query();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return MemberListGui.getInventory(company, list);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .abortIfNull()
                .sync((result) -> result.open(player))
                .execute();
    }

    private void openCompanyAccounts(Player player, String companyName) {
        Stonks.newChain()
                .asyncFirst(() -> {
                    player.sendMessage(ChatColor.AQUA + "Loading accounts...");
                    try {
                        Company company = databaseManager.getCompanyDao().getCompany(companyName);
                        if (company == null) {
                            player.sendMessage(ChatColor.RED + "That company doesn't exist!");
                            return null;
                        }
                        return AccountListGui.getInventory(company, databaseManager.getAccountLinkDao().getAccounts(company));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .abortIfNull()
                .sync((result) -> result.open(player))
                .execute();
    }

    private void openInvitesList(Player player) {
        Stonks.newChain()
                .asyncFirst(() -> {
                    List<Member> invites;
                    try {
                        invites = databaseManager.getMemberDao().getInvites(player);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }
                    if (invites == null) {
                        player.sendMessage(ChatColor.RED + "You don't have any invites!");
                        return null;
                    }
                    return InviteListGui.getInventory();
                })
                .abortIfNull()
                .sync((result) -> result.open(player))
                .execute();
    }

}
