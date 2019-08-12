package nl.tychovi.stonks.managers;

import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.AccountOwnerCheckEvent;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import nl.tychovi.stonks.Stonks;
import nl.tychovi.stonks.model.Company;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class ShopManager extends SpigotModule {

  public ShopManager(Stonks plugin) {
    super("Shop Manager", plugin);
  }

  @EventHandler
  public void onAccountQuery(AccountQueryEvent event) {
    if(event.getName().startsWith("#")) {
      DatabaseManager databaseManagager = (DatabaseManager) plugin.getModule("Database Manager");

      Company company = databaseManagager.getCompanyByName(event.getName());

      Account CSAccount = new Account("Tycho inc.", "Tycho inc.", company.id());
      event.setAccount(CSAccount);
    }
  }

  @EventHandler
  public void onAccountCheck(AccountCheckEvent event) {

  }

  @EventHandler
  public void onOwnEvent(AccountOwnerCheckEvent event) {
    Bukkit.broadcastMessage("--------------");
    Bukkit.broadcastMessage("Player: " + event.getPlayer().getName());
    Bukkit.broadcastMessage("AccountUuid: " + event.getAccount().getName());
    if(event.getPlayer().getName().equals("TychoVI")) {
      event.setOutcome(true);
    }
  }
}
