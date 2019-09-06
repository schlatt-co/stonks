package dev.tycho.stonks.command;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.gui.*;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.managers.GuiManager;
import dev.tycho.stonks.managers.MessageManager;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static dev.tycho.stonks.model.core.Role.*;

//TODO break this class up into sublcasses
// its almost 1000 lines long
public class CompanyCommand implements CommandExecutor {

  private DatabaseManager databaseManager;
  private GuiManager guiManager;
  private JavaPlugin plugin;
  private Essentials ess;

  private final long COMPANY_CREATION_COOLDOWN;
  private final long ACCOUNT_CREATION_COOLDOWN;

  private HashMap<UUID, Long> playerCompanyCooldown = new HashMap<>();
  private HashMap<UUID, Long> playerAccountCooldown = new HashMap<>();

  public CompanyCommand(DatabaseManager databaseManager, Stonks plugin) {
    this.databaseManager = databaseManager;
    this.plugin = plugin;
    this.ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
    this.guiManager = (GuiManager) plugin.getModule("guiManager");

    COMPANY_CREATION_COOLDOWN = plugin.getConfig().getLong("cooldowns.companycreation");
    ACCOUNT_CREATION_COOLDOWN = plugin.getConfig().getLong("cooldowns.accountcreation");
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
      return true;
    }

    Player player = (Player) sender;

    if (args.length == 0) {
      //If no arg is provided just list companies
      openCompanyList(player, OrderBy.NAMEASC);
      return true;
    }

    switch (args[0].toLowerCase()) {
      case "create": {
        if (args.length > 1) {
          String newName = concatArgs(1, args);
          double fee = plugin.getConfig().getDouble("fees.companycreation");
          new ConfirmationGui.Builder()
              .title(ChatColor.BOLD + "Accept $" + fee + " creation fee?")
              .onChoiceMade(b -> {
                if (b) createCompany(player, newName);
              })
              .open(player);
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " create <company>");
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
        openCompanyInfo(player, concatArgs(1, args));
        return true;
      }
      case "members": {
        if (args.length < 2) {
          player.sendMessage(ChatColor.RED + "Please specify a company!");
          return true;
        }
        openCompanyMembers(player, concatArgs(1, args));
        return true;
      }
      case "accounts": {
        if (args.length < 2) {
          player.sendMessage(ChatColor.RED + "Please specify a company!");
          return true;
        }
        openCompanyAccounts(player, concatArgs(1, args));
        return true;
      }
      case "invite": {
        if (args.length > 1) {
          String playerToInvite = args[1];
          List<Company> list = databaseManager.getCompanyDao()
              .getAllCompaniesWhereManager(player, databaseManager.getMemberDao().queryBuilder());
          new CompanySelectorGui.Builder()
              .companies(list)
              .title("Select a company to invite to")
              .companySelected((company -> invitePlayerToCompany(player, company.getName(), playerToInvite)))
              .open(player);
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /stonks invite <player>");
        }
        return true;
      }
      case "createaccount": { //stonks createaccount <account_name>
        if (args.length > 1) {
          String newName = concatArgs(1, args);
          new AccountTypeSelectorGui.Builder()
              .title("Select an account type")
              .typeSelected(type -> {
                    List<Company> list;
                    //Get all the accounts the player is a manager of
                    list = databaseManager.getCompanyDao()
                        .getAllCompaniesWhereManager(player,
                            databaseManager.getMemberDao().queryBuilder());
                    new CompanySelectorGui.Builder()
                        .title("Select a company")
                        .companies(list)
                        .companySelected(company -> {
                          double fee = plugin.getConfig().getDouble("fees.accountcreation");
                          new ConfirmationGui.Builder()
                              .title(ChatColor.BOLD + "Accept $" + fee + " creation fee?")
                              .onChoiceMade(b -> {
                                if (b) switch (type) {
                                  case HoldingsAccount:
                                    createHoldingsAccount(player, company.getName(), newName);
                                    break;
                                  case CompanyAccount:
                                    createCompanyAccount(player, company.getName(), newName);
                                    break;
                                }
                              })
                              .open(player);

                        })
                        .open(player);
                  }
              ).open(player);
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " createaccount <account_name>");
        }
        return true;
      }
      case "createholding": { // /comp createholding <player_name> <share>
        if (args.length > 2) {
          String playerName = args[1];
          double share = Double.parseDouble(args[2]);
          List<Company> list = databaseManager.getCompanyDao()
              .getAllCompaniesWhereManager(player, databaseManager.getMemberDao().queryBuilder());
          new CompanySelectorGui.Builder()
              .companies(list)
              .title("Select a company")
              .companySelected((company ->
                  new AccountSelectorGui.Builder()
                      .company(company)
                      .title("Select an account")
                      .accountSelected(l -> createHolding(player, l.getId(), playerName, share))
                      .open(player)))
              .open(player);
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " createholding <player_name> <share>");
        }
        return true;
      }
      case "removeholding": { // /comp removeholding <accountid> <player_name>
        if (args.length > 2) {
          removeHolding(player, Integer.parseInt(args[1]), args[2]);
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " removeholding <accountid> <player_name>");
        }
        return true;
      }
//      case "emptyholding": { // /comp emptyholding <accountid> <player_name>
//        if (args.length > 2) {
//          removeHolding(player, Integer.parseInt(args[1]), args[2]);
//        } else {
//          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " removeholding <accountid> <player_name>");
//        }
//        return true;
//      }
      case "withdraw": { // /comp withdraw <amount> [optional <accountid> ]
        if (args.length > 1) {
          double amount = Double.parseDouble(args[1]);
          if (args.length > 2) {
            withdrawFromAccount(player, amount, Integer.parseInt(args[2]));
          } else {
            //get all companies
            List<Company> list = databaseManager.getCompanyDao().getAllCompanies();

            //We need a list of all companies with a withdrawable account for this player
            //Remove companies where the player is not a manager and doesn't have an account
            //todo remove this messy logic
            for (int i = list.size() - 1; i >= 0; i--) {
              boolean remove = true;
              Company c = list.get(i);
              Member m = c.getMember(player);
              if (m != null && m.hasManagamentPermission()) {
                //If a manager or ceo
                remove = false;
              }
              //If you are not a manager, or a non-member with a holding then don't remove
              for (AccountLink a : c.getAccounts()) {
                //Is there a holding account for the player
                ReturningAccountVisitor<Boolean> visitor = new ReturningAccountVisitor<Boolean>() {
                  @Override
                  public void visit(CompanyAccount a) {
                    val = false;
                  }

                  @Override
                  public void visit(HoldingsAccount a) {
                    val = (a.getPlayerHolding(player.getUniqueId()) != null);
                  }
                };
                a.getAccount().accept(visitor);
                if (visitor.getRecentVal()) remove = false;
              }
              if (remove) list.remove(i);
            }


            new CompanySelectorGui.Builder()
                .companies(list)
                .title("Select a company to withdraw from")
                .companySelected((company ->
                    new AccountSelectorGui.Builder()
                        .company(company)
                        .title("Select an account to withdraw from")
                        .accountSelected(l -> withdrawFromAccount(player, amount, l.getId()))
                        .open(player)))
                .open(player);
          }
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " withdraw <amount> [optional <accountid> ]");
        }
        return true;
      }
      case "setlogo": {
        List<Company> list = databaseManager.getCompanyDao()
            .getAllCompaniesWhereManager(player, databaseManager.getMemberDao().queryBuilder());
        new CompanySelectorGui.Builder()
            .companies(list)
            .title("Select company logo to change")
            .companySelected((company -> setLogo(player, company.getName())))
            .open(player);
        return true;
      }
      case "pay": {
        if (args.length > 1) {
          double amount = Double.parseDouble(args[1]);
          final String message = (args.length > 2) ? concatArgs(2, args) : "";
          if (message.length() > 200) {
            player.sendMessage(ChatColor.RED + "Your message cannot be longer than 200 characters");
            return true;
          }
          //get all companies
          List<Company> list = databaseManager.getCompanyDao().getAllCompanies();
          new CompanySelectorGui.Builder()
              .companies(list)
              .title("Select a company to pay")
              .companySelected((company -> {
                //Cache the next screen
                AccountSelectorGui.Builder accountSelectorScreen =
                    new AccountSelectorGui.Builder()
                        .company(company)
                        .title("Select which account to pay")
                        .accountSelected(l -> payAccount(player, l.getId(), message, amount));
                List<String> info = new ArrayList<>();
                info.add("You are trying to pay an unverified company!");
                info.add("Unverified companies might be pretending to be ");
                info.add("someone else's company");
                info.add("Make sure you are paying the correct company");
                info.add("(e.g. by checking the CEO is who you expect)");
                info.add("To get a company verified, ask a moderator.");
                info.add("");
                info.add(ChatColor.GOLD + "The CEO of this company is ");
                String ceoName = "[error lol]";
                for (Member m : company.getMembers()) {
                  if (m.getRole().equals(CEO)) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(m.getUuid());
                    if (p != null) ceoName = p.getName();
                  }
                }
                info.add(ChatColor.GOLD + ceoName);
                if (!company.isVerified()) {
                  new ConfirmationGui.Builder()
                      .title(company.getName() + " is unverified")
                      .info(info)
                      .onChoiceMade(
                          c -> {
                            if (c) accountSelectorScreen.open(player);
                          }
                      ).open(player);
                } else {
                  accountSelectorScreen.open(player);
                }
              }))
              .open(player);
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " pay <amount> <message>");
        }
        return true;
      }
      case "setrole": { // /comp setrole <playername> <role> <company>
        if (args.length > 3) {
          String compName = concatArgs(3, args);
          setRole(player, args[1], args[2], compName);
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " setrole <player> <role> <company>");
        }
        return true;
      }
      case "memberinfo": {
        if (args.length > 2) {
          String compName = concatArgs(2, args);
          openMemberInfo(player, args[1], compName);
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " memberinfo <player> <company>");
        }
        return true;
      }
      case "kickmember": {
        if (args.length > 2) {
          String compName = concatArgs(2, args);
          kickMember(player, args[1], compName);
        } else {
          player.performCommand(ChatColor.RED + "Correct usage: /" + label + " kickmember <player> <company>");
        }
        return true;
      }
      case "fees": {
        player.sendMessage(ChatColor.GOLD + "----------------");
        player.sendMessage(ChatColor.AQUA + "Company creation: $" + plugin.getConfig().getDouble("fees.companycreation"));
        player.sendMessage(ChatColor.AQUA + "CompanyAccount creation: $" + plugin.getConfig().getDouble("fees.companyaccountcreation"));
        player.sendMessage(ChatColor.AQUA + "HoldingsAccount creation: $" + plugin.getConfig().getDouble("fees.holdingsaccountcreation"));
        player.sendMessage(ChatColor.GOLD + "----------------");
        return true;
      }
      case "holdinginfo": {
        if (args.length < 2) {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " holdinginfo <accountid>");
          return true;
        }
        openHoldingAccountInfo(player, Integer.parseInt(args[1]));
        return true;
      }
      case "top": {
        showTopCompanies(player);
        return true;
      }
      //Hidden commands
      case "rename": { // stonks rename <new name>
        if (args.length > 1) {
          if (player.isOp() || player.hasPermission("trevor.mod")) {
            String newName = concatArgs(1, args);
            new CompanySelectorGui.Builder()
                .title("Select company to rename")
                .companies(databaseManager.getCompanyDao().getAllCompanies())
                .companySelected(company -> {
                  new ConfirmationGui.Builder()
                      .title("Rename " + company.getName() + " to" + newName + "?")
                      .onChoiceMade(c -> {
                        if (c) renameCompany(player, company.getName(), newName);
                      })
                      .open(player);
                })
                .open(player);
          } else {
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this");
          }
        } else {
          player.sendMessage(ChatColor.RED + "Correct usage: /" + label + " rename <new name>");
        }
        return true;
      }
      case "verify": {
        if (player.isOp() || player.hasPermission("trevor.mod")) {
          new CompanySelectorGui.Builder()
              .title("Select company to verify")
              .companies(databaseManager.getCompanyDao().getAllCompanies())
              .companySelected(company -> {
                new ConfirmationGui.Builder()
                    .title("Verify " + company.getName() + "?")
                    .onChoiceMade(c -> {
                      if (c) changeVerification(player, company.getName(), true);
                    })
                    .open(player);
              })
              .open(player);
        } else {
          player.sendMessage(ChatColor.RED + "You don't have permissions to do this");
        }
        return true;
      }
      case "unverify": {
        if (player.isOp() || player.hasPermission("trevor.mod")) {
          new CompanySelectorGui.Builder()
              .title("Select company to unverify")
              .companies(databaseManager.getCompanyDao().getAllCompanies())
              .companySelected(company -> {
                new ConfirmationGui.Builder()
                    .title("Unverify " + company.getName() + "?")
                    .onChoiceMade(c -> {
                      if (c) changeVerification(player, company.getName(), false);
                    })
                    .open(player);
              })
              .open(player);
        } else {
          player.sendMessage(ChatColor.RED + "You don't have permissions to do this");
        }
        return true;
      }
      case "unhide": {
        if (player.isOp() || player.hasPermission("trevor.mod")) {
          List<Company> companies = new ArrayList<>();
          QueryBuilder<Company, UUID> queryBuilder = databaseManager.getCompanyDao().queryBuilder();
          queryBuilder.orderBy("name", true);
          try {
            queryBuilder.where().eq("hidden", true);
            companies = queryBuilder.query();
          } catch (SQLException e) {
            e.printStackTrace();
          }

          new CompanySelectorGui.Builder()
              .title("Select company to unhide")
              .companies(companies)
              .companySelected(company -> {
                new ConfirmationGui.Builder()
                    .title("Unhide " + company.getName() + "?")
                    .onChoiceMade(c -> {
                      if (c) changeHidden(player, company.getName(), false);
                    })
                    .open(player);
              })
              .open(player);
        } else {
          player.sendMessage(ChatColor.RED + "You don't have permissions to do this");
        }
        return true;
      }
      case "hide": {
        if (player.isOp() || player.hasPermission("trevor.mod")) {
          List<Company> companies = new ArrayList<>();
          QueryBuilder<Company, UUID> queryBuilder = databaseManager.getCompanyDao().queryBuilder();
          queryBuilder.orderBy("name", true);
          try {
            queryBuilder.where().eq("hidden", false);
            companies = queryBuilder.query();
          } catch (SQLException e) {
            e.printStackTrace();
          }

          new CompanySelectorGui.Builder()
              .title("Select company to hide")
              .companies(companies)
              .companySelected(company -> {
                new ConfirmationGui.Builder()
                    .title("Hide " + company.getName() + "?")
                    .onChoiceMade(c -> {
                      if (c) changeHidden(player, company.getName(), true);
                    })
                    .open(player);
              })
              .open(player);
        } else {
          player.sendMessage(ChatColor.RED + "You don't have permissions to do this");
        }
        return true;
      }
      case "listhidden": {
        if (player.isOp() || player.hasPermission("trevor.mod")) {
          Stonks.newChain()
              .asyncFirst(() -> {
                List<Company> companies = new ArrayList<>();
                QueryBuilder<Company, UUID> queryBuilder = databaseManager.getCompanyDao().queryBuilder();
                queryBuilder.orderBy("name", true);
                try {
                  queryBuilder.where().eq("hidden", true);
                  companies = queryBuilder.query();
                } catch (SQLException e) {
                  e.printStackTrace();
                }
                return new CompanyListGui(companies);
              }).sync((result) -> result.show(player))
              .execute();
        } else {
          player.sendMessage(ChatColor.RED + "You don't have permissions to do this");
        }
        return true;
      }
      case "history": {
        if (args.length == 2) {
          AccountLink link;
          try {
            link = databaseManager.getAccountLinkDao().queryForId(Integer.parseInt(args[1]));
          } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "SQL ERROR");
            return true;
          }

          if (link == null) {
            player.sendMessage(ChatColor.RED + "Account not found");
            return true;
          }
          new TransactionHistoryGui.Builder()
              .accountLink(link)
              .title("Transaction History")
              .open(player);
          return true;
        }
        if (args.length > 3) {
          if (args[1].equals("-v")) {
            TransactionHistoryPagination(player, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
          }
        } else {
          List<Company> list = databaseManager.getCompanyDao()
              .getAllCompaniesWhereManager(player, databaseManager.getMemberDao().queryBuilder());
          new CompanySelectorGui.Builder()
              .companies(list)
              .title("Select a company")
              .companySelected((company ->
                  new AccountSelectorGui.Builder()
                      .company(company)
                      .title("Select an account")
                      .accountSelected(l -> {
                        new TransactionHistoryGui.Builder()
                            .accountLink(l)
                            .title("Transaction History")
                            .open(player);
                      })
                      .open(player)))
              .open(player);
        }
        return true;
      }
    }

    MessageManager.sendHelpMessage(player, label);
    return true;
  }

  private void TransactionHistoryPagination(Player player, int accountId, int page) {
    AccountLink link;
    try {
      link = databaseManager.getAccountLinkDao().queryForId(accountId);
    } catch (SQLException e) {
      e.printStackTrace();
      player.sendMessage(ChatColor.RED + "SQL ERROR");
      return;
    }

    if (link == null) {
      player.sendMessage(ChatColor.RED + "Account not found");
      return;
    }

    List<Transaction> transactions = databaseManager.getTransactionDao()
        .getTransactionsForAccount(link, databaseManager.getAccountLinkDao().queryBuilder(), 10, 10 * page);
    if (transactions.size() == 0) {
      player.sendMessage("!");
      player.sendMessage(ChatColor.YELLOW + "==== No more pages ====");
      return;
    }
    int i = 0;
    player.sendMessage(ChatColor.YELLOW + "==== Page " + page + " ====");
    for (Transaction transaction : transactions) {
      StringBuilder s = new StringBuilder((i + page * 10)+ ")");
      s.append("[" + transaction.getId() + "] ");
      s.append("$" + transaction.getAmount() + " ");
      s.append(((transaction.getPayee() != null) ? transaction.getPayee() : "unknown") + " ");
      if (transaction.getMessage() != null) s.append(transaction.getMessage());
      player.sendMessage(s.toString());
      i++;
    }
    if (i < 10) player.sendMessage("!");
  }


  private String concatArgs(int startArg, String[] args) {
    StringBuilder concat = new StringBuilder();
    for (int i = startArg; i < args.length; i++) {
      if (i > startArg) concat.append(" ");
      concat.append(args[i]);
    }
    return concat.toString();
  }

  private void showTopCompanies(Player player) {
    player.sendMessage(ChatColor.AQUA + "Fetching company list, one moment...");
    Stonks.newChain()
        .async(() -> {
          List<Company> list = null;
          try {
            QueryBuilder<Company, UUID> companyQueryBuilder = databaseManager.getCompanyDao().queryBuilder();
            list = companyQueryBuilder.query();
            list.sort((c1, c2) -> (int) (c2.getTotalValue() - c1.getTotalValue()));

            player.sendMessage(ChatColor.AQUA + "--------------------");
            for (int i = 0; i < Math.min(10, list.size()); i++) {
              Company company = list.get(i);
              player.sendMessage(ChatColor.GOLD + String.valueOf(i + 1) + " - " + company.getName() + ": " + ChatColor.GREEN + "$" + Util.commify(company.getTotalValue()));
            }
            player.sendMessage(ChatColor.AQUA + "--------------------");
          } catch (SQLException e) {
            e.printStackTrace();
          }
        })
        .execute();
  }

  private void renameCompany(Player player, String companyName, String newCompanyName) {
    Stonks.newChain()
        .async(() -> {
          if (newCompanyName.length() > 32) {
            player.sendMessage(ChatColor.RED + "A company name can't be longer than 32 characters!");
            return;
          }
          Company company;
          try {
            company = databaseManager.getCompanyDao().getCompany(companyName);
          } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "SQL error tell wheezy");
            return;
          }
          //Find the company they are making the changes in
          if (company != null) {
            if (player.hasPermission("trevor.mod") || player.isOp()) {
              company.setName(newCompanyName);
              try {
                databaseManager.getCompanyDao().update(company);
                player.sendMessage(ChatColor.GREEN + "Company name updated");
              } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "SQL error tell wheezy");
                e.printStackTrace();
              }
            } else {
              player.sendMessage(ChatColor.RED + "You do not have the required permissions to change a company name");
            }


          } else {
            player.sendMessage(ChatColor.RED + "Company does not exist");
          }
        }).execute();
  }

  private void changeHidden(Player player, String companyName, boolean newHidden) {
    Stonks.newChain()
        .async(() -> {
          Company company;
          try {
            company = databaseManager.getCompanyDao().getCompany(companyName);
          } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "SQL error tell wheezy");
            return;
          }
          //Find the company they are making the changes in
          if (company != null) {
            if (player.hasPermission("trevor.mod") || player.isOp()) {
              company.setHidden(newHidden);
              try {
                databaseManager.getCompanyDao().update(company);
                player.sendMessage(ChatColor.GREEN + "Company " + ((company.isHidden()) ? "hidden" : "un-hidden"));
              } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "SQL error tell wheezy");
                e.printStackTrace();
              }
            } else {
              player.sendMessage(ChatColor.RED + "You do not have the required permissions to change verification");
            }
          } else {
            player.sendMessage(ChatColor.RED + "Company does not exist");
          }
        }).execute();
  }

  private void changeVerification(Player player, String companyName, boolean newVerification) {
    Stonks.newChain()
        .async(() -> {
          Company company;
          try {
            company = databaseManager.getCompanyDao().getCompany(companyName);
          } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "SQL error tell wheezy");
            return;
          }
          //Find the company they are making the changes in
          if (company != null) {
            if (player.hasPermission("trevor.mod") || player.isOp()) {
              company.setVerified(newVerification);
              try {
                databaseManager.getCompanyDao().update(company);
                player.sendMessage(ChatColor.GREEN + "Company verification updated");
              } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "SQL error tell wheezy");
                e.printStackTrace();
              }
            } else {
              player.sendMessage(ChatColor.RED + "You do not have the required permissions to change verification");
            }


          } else {
            player.sendMessage(ChatColor.RED + "Company does not exist");
          }
        }).execute();
  }

  private void openHoldingAccountInfo(Player player, int accountId) {
    Stonks.newChain()
        .asyncFirst(() -> {
          try {
            AccountLink link;
            link = databaseManager.getAccountLinkDao().queryForId(accountId);
            if (!(link.getAccount() instanceof HoldingsAccount)) {
              player.sendMessage(ChatColor.RED + "You can only view holdings for holdingsaccounts.");
              return null;
            }
            return new HoldingListGui((HoldingsAccount) link.getAccount());
          } catch (SQLException e) {
            e.printStackTrace();
          }
          return null;
        })
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }

  private void removeHolding(Player player, int accountId, String playerName) {
    //noinspection Convert2MethodRef
    Stonks.newChain()
        .async(() -> {
          try {
            AccountLink link = databaseManager.getAccountLinkDao().queryForId(accountId);
            if (link != null) {
              //We have a valid account
              //First make sure the account is a holdings account
              if (link.getAccount() instanceof HoldingsAccount) {
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
                          if (account.getHoldings().size() < 2) {
                            player.sendMessage(ChatColor.RED + "There always needs to be at least one holding per holdingsaccount!");
                            return;
                          }
                          account.removeHolding(playerHolding);
                          databaseManager.getHoldingDao().delete(playerHolding);
                          player.sendMessage(ChatColor.GREEN + "Holding successfully removed!");
                        } else {
                          player.sendMessage(ChatColor.RED + "There is more than $1 in that holding");
                          player.sendMessage(ChatColor.RED + "Please get " + playerName +
                              " to withdraw so there is less than $1 remaining.");
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
        })
        .sync(() -> player.closeInventory())
        .execute();
  }

  private void setRole(Player player, String playerName, String roleString, String companyName) {
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
          //Now see if the player to promote exists
          Player playerToChange = ess.getUser(playerName).getBase();
          if (playerToChange != null) {
            Company company;
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
                        OfflinePlayer p = Bukkit.getOfflinePlayer(memberToChange.getUuid());
                        if (p.isOnline() && p.getPlayer() != null) p.getPlayer().sendMessage(ChatColor.YELLOW + "Your rank in the company " + company.getName() + " has changed to " + newRole.toString());
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
              if (link.getAccount() instanceof HoldingsAccount) {
                HoldingsAccount account = (HoldingsAccount) link.getAccount();
                //Is the player a member of that company
                if (member != null) {
                  //Does the player have permission to create a holding in that account?
                  if (member.hasManagamentPermission()) {
                    //Try and find the UUID of that player
                    User u = ess.getOfflineUser(playerName);
                    //check if the player has been on the server
                    if (u != null) {
                      Player newHoldingOwner = ess.getOfflineUser(playerName).getBase();
                      //Check they are a member of that company
                      if (link.getCompany().hasMember(newHoldingOwner)) {
                        if (account.getPlayerHolding(newHoldingOwner.getUniqueId()) == null) {
                          //We can make a holding
                          Holding holding = new Holding(newHoldingOwner.getUniqueId(), share, account);
                          databaseManager.getHoldingDao().create(holding);
                          player.sendMessage(ChatColor.GREEN + "Holding successfully created!");
                        } else {
                          player.sendMessage(ChatColor.RED + "The player you are making a holding for already has a holding in this account");
                        }
                      } else {
                        player.sendMessage(ChatColor.RED + "The player you are making a holding for is not a member of the company");
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
                          databaseManager.logTransaction(new Transaction(link, player.getUniqueId(), null, -amount));

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

  //turn createcompany and createholdings account into one method
  private void createCompanyAccount(Player player, String companyName, String accountName) {
    if (!player.isOp() && playerAccountCooldown.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - playerAccountCooldown.get(player.getUniqueId())) < ACCOUNT_CREATION_COOLDOWN) {
      player.sendMessage(ChatColor.RED + "You cannot make an account for another " +
          Util.convertString(ACCOUNT_CREATION_COOLDOWN - (System.currentTimeMillis() - playerAccountCooldown.get(player.getUniqueId()))));
      return;
    }
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
                    double creationFee = plugin.getConfig().getDouble("fees.companyaccountcreation");
                    if (!Stonks.economy.withdrawPlayer(player, creationFee).transactionSuccess()) {
                      player.sendMessage(ChatColor.RED + "There is a $" + creationFee + " fee for creating a companyaccount and you do not have sufficient funds, get some more money you poor fuck.");
                      return;
                    }
                    CompanyAccount ca = new CompanyAccount(accountName);
                    databaseManager.getCompanyAccountDao().create(ca);

                    AccountLink link = new AccountLink(company, ca);
                    databaseManager.getAccountLinkDao().create(link);

                    player.sendMessage(ChatColor.GREEN + "Company account '" + accountName + "' added to '" + companyName + "' !");

                    playerAccountCooldown.put(player.getUniqueId(), System.currentTimeMillis());

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

    if (!player.isOp() && playerAccountCooldown.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - playerAccountCooldown.get(player.getUniqueId())) < ACCOUNT_CREATION_COOLDOWN) {
      player.sendMessage(ChatColor.RED + "You cannot make an account for another " +
          Util.convertString(ACCOUNT_CREATION_COOLDOWN - (System.currentTimeMillis() - playerAccountCooldown.get(player.getUniqueId()))));
      return;
    }

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
                    double creationFee = plugin.getConfig().getDouble("fees.companyaccountcreation");
                    if (!Stonks.economy.withdrawPlayer(player, creationFee).transactionSuccess()) {
                      player.sendMessage(ChatColor.RED + "There is a $" + creationFee + " fee for creating a holdingsaccount and you do not have sufficient funds, get some more money you poor fuck.");
                      return;
                    }
                    HoldingsAccount ha = new HoldingsAccount(accountName);
                    databaseManager.getHoldingAccountDao().create(ha);

                    AccountLink link = new AccountLink(company, ha);
                    databaseManager.getAccountLinkDao().create(link);

                    //Make first holding
                    Holding holding = new Holding(player.getUniqueId(), 1, ha);
                    databaseManager.getHoldingDao().create(holding);

                    player.sendMessage(ChatColor.GREEN + "Holdings account '" + accountName + "' added to '" + companyName + "' !");

                    playerAccountCooldown.put(player.getUniqueId(), System.currentTimeMillis());
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

  private void kickMember(Player player, String memberName, String companyName) {
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
            Bukkit.broadcastMessage(ChatColor.BOLD + "" +  ChatColor.RED + memberName + " has been fired from " + company.getName() + "!");
//            OfflinePlayer p = Bukkit.getOfflinePlayer(memberToKick.getUuid());
//            if (p.isOnline() && p.getPlayer() != null) p.getPlayer().sendMessage(ChatColor.RED + "You have been fired from " + company.getName() + "!");
          } catch (SQLException e) {
            e.printStackTrace();
          }
        })
        .sync(() -> player.performCommand("stonks members " + companyName))
        .execute();
  }

  private void openMemberInfo(Player player, String memberName, String companyName) {
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

  private void payAccount(Player sender, int accountId, String message, double amount) {
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
          databaseManager.logTransaction(new Transaction(accountLink, sender.getUniqueId(), message, amount));
          //Tell the user we paid the account
          sender.sendMessage(ChatColor.GREEN + "Paid " + ChatColor.DARK_GREEN + accountLink.getCompany().getName() +
              " (" + accountLink.getAccount().getName() + ")" + ChatColor.GREEN + " $" + Util.commify(amount) + "!");

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
          Company company;
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

  private void createCompany(Player player, String companyName) {
    //Prevent the player from spamming companies
    if (!player.isOp() && playerCompanyCooldown.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - playerCompanyCooldown.get(player.getUniqueId())) < COMPANY_CREATION_COOLDOWN) {
      player.sendMessage(ChatColor.RED + "You cannot make a company for another " +
          Util.convertString(COMPANY_CREATION_COOLDOWN - (System.currentTimeMillis() - playerCompanyCooldown.get(player.getUniqueId()))));
      return;
    }
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
            double creationFee = plugin.getConfig().getDouble("fees.companycreation");
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
            playerCompanyCooldown.put(player.getUniqueId(), System.currentTimeMillis());
            Bukkit.broadcastMessage(ChatColor.GREEN + player.getDisplayName() + ChatColor.GREEN + " just founded a new company - " + newCompany.getName() + "!");

          } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Something went wrong! :(");
          }
        }).execute();
  }

  private void invitePlayerToCompany(Player player, String companyName, String playerToInvite) {
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
            playerToInviteObject.sendMessage(ChatColor.GOLD + "You have been invited to join " + companyName);
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }).execute();
  }

  @SuppressWarnings("SameParameterValue")
  private void openCompanyList(Player player, OrderBy orderBy) {
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
            }
            list = companyQueryBuilder.query();

            //remove hidden companies from the list if a player is not a member
            //todo move this into a query
            for (int i = list.size() - 1; i >= 0; i--) {
              if (list.get(i).isHidden() && list.get(i).getMember(player) == null) {
                list.remove(i);
              }
            }

          } catch (SQLException e) {
            e.printStackTrace();
          }
          return new CompanyListGui(list);
        })
        .abortIfNull()
        .sync((result) -> (result).show(player))
        .execute();
  }

  @SuppressWarnings("unused")
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
        .sync(gui -> gui.open(player))
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
            return new AccountListGui(company);
          } catch (SQLException e) {
            e.printStackTrace();
          }
          return null;
        })
        .abortIfNull()
        .sync(gui->gui.show(player))
        .execute();
  }

  private void openInvitesList(Player player) {
    Stonks.newChain()
        .asyncFirst(() -> {
          List<Member> invites;
            invites = databaseManager.getMemberDao().getInvites(player);
          if (invites == null) {
            player.sendMessage(ChatColor.RED + "You don't have any invites!");
            return null;
          }
          return new InviteListGui(player);
        })
        .abortIfNull()
        .sync(gui -> gui.show(player))
        .execute();
  }

}
