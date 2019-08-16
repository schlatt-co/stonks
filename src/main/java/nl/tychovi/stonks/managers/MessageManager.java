package nl.tychovi.stonks.managers;

import nl.tychovi.stonks.Database.Member;
import nl.tychovi.stonks.Stonks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.List;

public class MessageManager extends SpigotModule {
    private DatabaseManager databaseManager;

    public MessageManager(Stonks plugin) {
        super("MessageManager", plugin);
        this.databaseManager = (DatabaseManager) plugin.getModule("databaseManager");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        List<Member> invites = databaseManager.getMemberDao().getInvites(event.getPlayer());
        if(invites != null) {
            for(Member invite : invites) {
                event.getPlayer().sendMessage("You have been invited to company: " + invite.getCompany().getName());
            }
        }
    }
}
