package dev.tycho.stonks.command;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.stonks.Database.*;
import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.gui.*;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.managers.GuiManager;
import dev.tycho.stonks.managers.MessageManager;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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

    if(args.length == 0) {
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
            if(args.length < 2) {
                player.sendMessage(ChatColor.RED + "Please specify a company!");
                return true;
            }
            openCompanyInfo(player, args[1]);
            return true;
        }
        case "members": {
            if(args.length < 2) {
                player.sendMessage(ChatColor.RED + "Please specify a company!");
                return true;
            }
            openCompanyMembers(player, args[1]);
            return true;
        }
        case "accounts": {
            if(args.length < 2) {
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
        case "createcompanyaccount": {
            return true;
        }
        case "setlogo": {
            if(args.length < 2) {
                player.sendMessage(ChatColor.RED + "Please specify a company!");
                return true;
            }
            setLogo(player, args[1]);
            return true;
        }
        case "pay": {
            if(args.length < 3) {
                player.sendMessage(ChatColor.RED + "Correct usage: /stonks pay <amount> <accountid>");
                return true;
            }
            payAccount(Double.parseDouble(args[1]), Integer.parseInt(args[2]), player);
            return true;
        }
        case "memberinfo": {
            if(args.length < 3) {
                player.sendMessage(ChatColor.RED + "Correct usage: /stonks memberinfo <player> <company>");
                return true;
            }
            openMemberInfo(args[1], args[2], player);
            return true;
        }
        case "kickmember": {
            if(args.length < 3) {
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

  private void kickMember(String memberName, String companyName, Player player) {
      Stonks.newChain()
              .async(() -> {
                  try {
                      User u = ess.getUser(memberName);
                      if(u == null) {
                          player.sendMessage(ChatColor.RED + "That player could not be found!");
                          return;
                      }
                      Player playerProfile = u.getBase();
                      Company company = databaseManager.getCompanyDao().getCompany(companyName);

                      if(company == null) {
                          player.sendMessage(ChatColor.RED + "That player/company could not be found!");
                          return;
                      }

                      Member memberToKick = databaseManager.getMemberDao().getMember(playerProfile, company);
                      if(memberToKick == null) {
                          player.sendMessage(ChatColor.RED + "That player isn't a member of that company!");
                          return;
                      }

                      Member sender = databaseManager.getMemberDao().getMember(player, company);
                      if(sender == null || !sender.hasManagamentPermission()) {
                          player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                          return;
                      }
                      if(memberToKick.getRole() == Role.CEO) {
                          player.sendMessage(ChatColor.RED + "You can't kick a CEO!");
                          return;
                      }

                      if(memberToKick.hasHoldings(databaseManager)) {
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

                      if(company == null || playerProfile == null) {
                          player.sendMessage(ChatColor.RED + "That player/company could not be found!");
                          return null;
                      }

                      Member member = databaseManager.getMemberDao().getMember(playerProfile, company);
                      if(member == null) {
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
                  if(accountLink == null) {
                      sender.sendMessage(ChatColor.RED + "That account doesn't exist!");
                  }

                  if(!Stonks.economy.withdrawPlayer(sender, amount).transactionSuccess()) {
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
                              e.printStackTrace();
                          }
                      }

                      @Override
                      public void visit(HoldingsAccount a) {
                          try {
                              databaseManager.getHoldingAccountDao().update(a);
                              for (Holding h: a.getHoldings()) {
                                  databaseManager.getHoldingDao().update(h);
                              }
                          } catch (SQLException e) {
                              e.printStackTrace();
                          }
                      }
                  };
                  accountLink.getAccount().accept(visitor);
                  sender.sendMessage(ChatColor.GREEN + "Payment processed successfully!");
                  return;
              }).execute();
  }

  private void setLogo(Player player, String companyName) {
      ItemStack itemInHand = player.getInventory().getItemInMainHand();
      if(itemInHand.getAmount() == 0) {
          player.sendMessage(ChatColor.RED + "You must be holding an item to set it as your company logo!");
          return;
      }
      Stonks.newChain()
              .async(() -> {
                  Company company = null;
                  try {
                      company = databaseManager.getCompanyDao().getCompany(companyName);
                      if(company == null) {
                          player.sendMessage(ChatColor.RED + "That company does not exist!");
                          return;
                      }
                      if(!company.hasMember(player)) {
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
                  if(companyName.length() > 32) {
                      player.sendMessage(ChatColor.RED + "A company name can't be longer than 32 characters!");
                      return;
                  }
                  try {
                      if(databaseManager.getCompanyDao().companyExists(companyName)) {
                          player.sendMessage(ChatColor.RED + "A company with that name already exists!");
                          return;
                      }
                      double creationFee = plugin.getConfig().getInt("fees.companycreation");
                      if(!Stonks.economy.withdrawPlayer(player, creationFee).transactionSuccess()) {
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

                      Member creator = new Member(player, Role.CEO);
                      newCompany.getMembers().add(creator);

                      player.sendMessage(ChatColor.GREEN + "Company with name: \"" + companyName + "\" created successfully!");
                  } catch (SQLException e) {
                      e.printStackTrace();
                      player.sendMessage(ChatColor.RED + "Something went wrong! :(");
                  }
              }).execute();
  }

  private Boolean invitePlayerToCompany(String playerToInvite, String companyName, Player player) {
      Stonks.newChain()
              .async(() -> {
                  try {
                      Player playerToInviteObject = ess.getUser(playerToInvite).getBase();

                      QueryBuilder<Company, UUID> queryBuilder = databaseManager.getCompanyDao().queryBuilder();
                      queryBuilder.where().eq("name", companyName);
                      List<Company> companies = queryBuilder.query();
                      if(companies.isEmpty()) {
                          player.sendMessage(ChatColor.RED + "That company does not exist.");
                          return;
                      }
                      if(companies.get(0).getMember(player) == null) {
                          player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                          return;
                      }
                      if(!companies.get(0).getMember(player).hasManagamentPermission()) {
                          player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                          return;
                      }
                      Member newMember = new Member(playerToInviteObject, Role.Employee, companies.get(0));
                      QueryBuilder<Member, UUID> checkQueryBuilder = databaseManager.getMemberDao().queryBuilder();
                      checkQueryBuilder.where().eq("uuid", newMember.getUuid()).and().eq("company_id", newMember.getCompany().getId());
                      List<Member> list = checkQueryBuilder.query();
                      if(!list.isEmpty()) {
                          if(list.get(0).getAcceptedInvite()) {
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
                      switch(orderBy) {
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
                      for(Company company : list) {
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
                      if(company == null) {
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
                        if(company == null) {
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
                    try {
                        Company company = databaseManager.getCompanyDao().getCompany(companyName);
                        if(company == null) {
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
                  if(invites == null) {
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
