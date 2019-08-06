package nl.tychovi.stonks.managers;

import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopManager extends SpigotModule {

  public ShopManager(JavaPlugin plugin) {
    super("Shop Manager", plugin);
  }

  @EventHandler
  public static void onPreShopCreationTakeover(PreShopCreationEvent event) {
    String nameLine = event.getSignLine(ChestShopSign.NAME_LINE);
    if (nameLine.equals("#company")) {
      event.setSignLine(ChestShopSign.NAME_LINE, "big yeet");
    }
  }
}
