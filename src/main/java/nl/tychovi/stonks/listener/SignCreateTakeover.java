package nl.tychovi.stonks.listener;

import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

public class SignCreateTakeover implements Listener {

  @EventHandler
  public static void onPreShopCreationTakeover(PreShopCreationEvent event) {
    String nameLine = event.getSignLine(NAME_LINE);
    if (nameLine.equals("#company")) {
      //open some sort of gui with all companies user is a member of
      event.setSignLine(NAME_LINE, "big yeet");
    }
  }
}
