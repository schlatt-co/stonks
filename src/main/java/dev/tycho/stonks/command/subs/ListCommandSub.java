package dev.tycho.stonks.command.subs;

import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.managers.DatabaseHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCommandSub extends CommandSub {

    public ListCommandSub() {
        super(false);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
        return null;
    }

    @Override
    public void onCommand(Player player, String alias, String[] args) {

        if (args.length > 1) {
            switch (args[1]) {
                case "all":
                    DatabaseHelper.getInstance().openCompanyList(player, CompanyListOptions.ALL);
                    return;
                case "member-of":
                    DatabaseHelper.getInstance().openCompanyList(player, CompanyListOptions.MEMBER_OF);
                    return;
                case "not-hidden":
                    DatabaseHelper.getInstance().openCompanyList(player, CompanyListOptions.NOT_HIDDEN_OR_MEMBER);
                    return;
                case "verified":
                    DatabaseHelper.getInstance().openCompanyList(player, CompanyListOptions.VERIFIED);
                    return;
            }
        }
        //default to not hidden
        DatabaseHelper.getInstance().openCompanyList(player, CompanyListOptions.getDefault());
    }

    public enum CompanyListOptions {
        ALL,
        NOT_HIDDEN_OR_MEMBER,
        VERIFIED,
        MEMBER_OF;

        public static CompanyListOptions getDefault() {
            return NOT_HIDDEN_OR_MEMBER;
        }

    }

}
