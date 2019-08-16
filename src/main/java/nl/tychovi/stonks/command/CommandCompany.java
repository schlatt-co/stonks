package nl.tychovi.stonks.command;

import com.earth2me.essentials.Essentials;
import net.ess3.api.IEssentials;
import nl.tychovi.stonks.Database.Company;
import nl.tychovi.stonks.Database.Member;
import nl.tychovi.stonks.Database.Role;
import nl.tychovi.stonks.Stonks;
import nl.tychovi.stonks.gui.InvitesGui;
import nl.tychovi.stonks.managers.DatabaseManager;
import nl.tychovi.stonks.managers.GuiManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;

public class CommandCompany implements CommandExecutor {

  private DatabaseManager databaseManager;
  private GuiManager guiManager;
  private JavaPlugin plugin;
  private Essentials ess;

  public CommandCompany(DatabaseManager databaseManager, Stonks plugin) {
    this.databaseManager = databaseManager;
    this.plugin = plugin;
    this.ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
    guiManager = (GuiManager) plugin.getModule("guiManager");
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
      player.sendMessage("You used /company!");
      return true;
    }

    switch (args[0].toLowerCase()) {
        case "create": {
            if (args[1] != null) {
              return companyCreate(args[1], player);
            } else {
              player.sendMessage(ChatColor.RED + "Please enter a company name");
              return true;
            }
        }
        case "invites": {
            List<Member> invites = null;
            try {
              invites = databaseManager.getMemberDao().getInvites(player);
            } catch (SQLException e) {
              e.printStackTrace();
            }
            if(invites == null) {
              player.sendMessage(ChatColor.RED + "You don't have any invites!");
              return true;
            }
            ((InvitesGui) guiManager.getGui("Invites")).initializeItems(invites);
            guiManager.getGui("Invites").openInventory(player);
            return true;
        }
        case "list": {
            player.sendMessage("----------");
            for (Company company : Stonks.companies) {
              player.sendMessage("-" + company.getName());
            }
            player.sendMessage("----------");
            return true;
        }
        case "manage": {
            if (args[1] != null) {
              switch (args[1].toLowerCase()) {
                case "invite":
                  if (args[2] != null && args[3] != null) {
                    return invitePlayerToCompany(args[2], args[3], player);
                  } else {
                    player.sendMessage(ChatColor.RED + "Please specify a player you want to invite and the company you want to invite them to.");
                  }
                  break;
              }
            } else {
              //add manage command options here
              return true;
            }
        }
        break;
    }
    return false;
  }
  
  private Boolean companyCreate(String companyName, Player player) {
    if(Stonks.companies.size() != 0) {
      for(Company company : Stonks.companies) {
        if(company.getName().equals(companyName)) {
          player.sendMessage(ChatColor.RED + "A company with that name already exists!");
          return true;
        }
      }
    }
    try {
      double creationFee = plugin.getConfig().getInt("fees.companycreation");
      if(!Stonks.economy.withdrawPlayer(player, creationFee).transactionSuccess()) {
        player.sendMessage(ChatColor.RED + "There is a $" + creationFee + " fee for creating a company and you did not have sufficient funds, get more money you poor fuck.");
        return true;
      }
      Company newCompany = new Company(companyName, "S" + companyName, player);
      databaseManager.getCompanyDao().assignEmptyForeignCollection(newCompany, "members");
      Stonks.companies.add(newCompany);
      databaseManager.getCompanyDao().create(newCompany);

      Member creator = new Member(player, Role.CEO);
      newCompany.getMembers().add(creator);

      player.sendMessage(ChatColor.GREEN + "Company with name: \"" + companyName + "\" created successfully!");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      player.sendMessage(ChatColor.RED + "Something went wrong! :(");
      return false;
    }
  }

  private Boolean invitePlayerToCompany(String playerToInvite, String companyName, Player player) {
    if(Stonks.companies.size() != 0) {
      for(Company company : Stonks.companies) {
        if(company.getName().equals(companyName)) {
          if(company.hasMember(player)) {
            Member playerProfile = company.getMember(player);
            if(playerProfile.hasManagamentPermission()) {
              Player playerToInviteObject = ess.getUser(playerToInvite).getBase();
              Member newMember = new Member(playerToInviteObject, Role.Slave, company);
              try {
                databaseManager.getMemberDao().create(newMember);
                player.sendMessage(playerToInviteObject.getName() + " has successfully been invited!");
                playerToInviteObject.sendMessage("You have been invited to join " + companyName);
                return true;
              } catch (SQLException e) {
                e.printStackTrace();
              }
            }
            return true;
          }
        }
      }
    }
    return false;
  }
}
