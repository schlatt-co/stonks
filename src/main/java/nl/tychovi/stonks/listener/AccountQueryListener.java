package nl.tychovi.stonks.listener;

import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AccountQueryListener implements Listener {

    @EventHandler
    public static void onAccountQuery(AccountQueryEvent event) {
        System.out.println(event.getName());
        System.out.println("BIG YEET");
    }
}
