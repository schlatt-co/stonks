package dev.tycho.stonks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabCompleterCompany implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return List.of("create",
                "invites",
                "list",
                "info",
                "members",
                "accounts",
                "invite",
                "createaccount",
//                "createcompanyaccount",
//                "createholdingsaccount",
                "createholding",
                "removeholding",
                "withdraw",
                "setlogo",
                "pay",
                "setrole",
                "memberinfo",
                "kickmember",
                "fees",
                "holdinginfo");
    }
}
