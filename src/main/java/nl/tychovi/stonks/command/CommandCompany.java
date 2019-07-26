package nl.tychovi.stonks.command;

import nl.tychovi.stonks.util.DatabaseConnector;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCompany implements CommandExecutor {

    private DatabaseConnector connector;

    public CommandCompany(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
            return true;
        }

        if(args[0].toLowerCase().equals("create") && args[1] != null) {
            if(createCompany(args[1], sender)) {
                sender.sendMessage(ChatColor.GREEN + "Company created successfully!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Something went wrong, please try again later.");
                return false;
            }
        } else if(args[0].toLowerCase().equals("list")) {
            return listCompanies(sender);
        }

        return true;
    }

    private Boolean createCompany(String name, CommandSender sender) {
        String uuid = ((Player)sender).getUniqueId().toString();

        return connector.createCompany(name, uuid);
    }

    private Boolean listCompanies(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "----------------");
        for(String name : connector.listCompanies()) {
            sender.sendMessage(ChatColor.GREEN + name);
        }
        sender.sendMessage(ChatColor.GOLD + "----------------");
        return true;
    }
}
