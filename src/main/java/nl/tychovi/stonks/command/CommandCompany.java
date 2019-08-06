package nl.tychovi.stonks.command;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import nl.tychovi.stonks.gui.CompanySelectorGUI;
import nl.tychovi.stonks.managers.DatabaseManager;
import nl.tychovi.stonks.model.Account;
import nl.tychovi.stonks.model.Company;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CommandCompany implements CommandExecutor {

  private DatabaseManager databaseManager;
  private InventoryManager inventoryManager;

  public CommandCompany(DatabaseManager databaseManager, InventoryManager inventoryManager) {
    this.databaseManager = databaseManager;
    this.inventoryManager = inventoryManager;
  }

  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
      return true;
    }
    switch (args[0].toLowerCase()) {
      case "edit":
        SmartInventory INVENTORY = SmartInventory.builder()
            .id("customInventory")
            .provider(new CompanySelectorGUI(databaseManager))
            .manager(inventoryManager)
            .size(4, 9)
            .title(ChatColor.YELLOW + "Company Edit")
            .closeable(true)
            .build();
        INVENTORY.open((Player) sender);
        return true;
      case "create":
        if (args[1] != null) {
          if (createCompany(args[1], sender)) {
            sender.sendMessage(ChatColor.GREEN + "Company created successfully!");
            return true;
          } else {
            sender.sendMessage(ChatColor.RED + "Something went wrong, please try again later.");
            return false;
          }
        } else {
          sender.sendMessage(ChatColor.RED + "Please enter a company name");
          return false;
        }
      case "list":
        return listCompanies(sender);
      case "addaccount":
        if (args[1] != null) {
          String newName;
          String companyName;
          if (args[2] != null) {
            newName = args[2];
            if (args[3] != null) {
              companyName = args[3];
            } else {
              sender.sendMessage(ChatColor.RED + "Please enter a company name");
              return false;
            }
          } else {
            sender.sendMessage(ChatColor.RED + "Please enter an account name");
            return false;
          }
          if (args[1].toLowerCase().equals("holding")) {

          } else if (args[1].toLowerCase().equals("company")) {
            Company c = databaseManager.getCompanyByName(companyName);
            String uuid = ((Player) sender).getUniqueId().toString();
            if (c == null) {
              sender.sendMessage(ChatColor.RED + "That company does not exist");
              return false;
            }
            if (databaseManager.createCompanyAccountObject(c, newName, uuid)) {
              sender.sendMessage(ChatColor.GREEN + "Account created!");
              return true;
            } else {
              return false;
            }
          }

        }
        break;
    }
    sender.sendMessage(ChatColor.RED + "Not like that PLACEHOLDER");
    return true;
  }

  private Boolean createCompany(String name, CommandSender sender) {
    String uuid = ((Player) sender).getUniqueId().toString();
    return databaseManager.createCompanyObject(name, uuid);
  }

  private Boolean listCompanies(CommandSender sender) {
    sender.sendMessage(ChatColor.GOLD + "----------------");
    for (Company c : databaseManager.getCachedCompanies()) {
      sender.sendMessage(ChatColor.GREEN + c.getName());
      for (Account a : c.getAccounts()) {
        sender.sendMessage(ChatColor.GREEN + " - " + a.getName() + " $" + a.getBalance());
      }
    }
    sender.sendMessage(ChatColor.GOLD + "----------------");
    return true;
  }
}
