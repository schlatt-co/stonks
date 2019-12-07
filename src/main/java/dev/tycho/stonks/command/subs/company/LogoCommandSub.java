package dev.tycho.stonks.command.subs.company;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class LogoCommandSub extends CommandSub {

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

  @Override
  public void onCommand(Player player, String alias, String[] args) {
    Collection<Company> list = Repo.getInstance().companiesWhereManager(player);
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select company logo to change")
        .companySelected((company -> setLogo(player, company)))
        .open(player);
  }

  private void setLogo(Player player, Company company) {
    ItemStack itemInHand = player.getInventory().getItemInMainHand();
    if (itemInHand.getAmount() == 0) {
      sendMessage(player, "You must hold the item you wish to set as the company icon!");
      return;
    }
    Stonks.newChain()
        .async(() -> {
          String newLogoMaterial = itemInHand.getType().name();
            Repo.getInstance().modifyCompany(company, company.name, newLogoMaterial, company.verified, company.hidden);
            sendMessage(player, "Company logo updated successfully!");
        })
        .execute();
  }
}
