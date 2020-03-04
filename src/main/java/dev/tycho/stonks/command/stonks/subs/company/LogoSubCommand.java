package dev.tycho.stonks.command.stonks.subs.company;

import dev.tycho.stonks.command.base.SimpleSubCommand;
import dev.tycho.stonks.gui.CompanySelectorGui;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class LogoSubCommand extends SimpleSubCommand {

  @Override
  public void execute(Player player) {
    Collection<Company> list = Repo.getInstance().companiesWhereManager(player);
    new CompanySelectorGui.Builder()
        .companies(list)
        .title("Select company logo to change")
        .companySelected((company -> setLogo(player, company)))
        .show(player);
  }

  private void setLogo(Player player, Company company) {
    ItemStack itemInHand = player.getInventory().getItemInMainHand();
    if (itemInHand.getAmount() == 0) {
      sendMessage(player, "You must hold the item you wish to set as the company icon!");
      return;
    }
    String newLogoMaterial = itemInHand.getType().name();
    Repo.getInstance().modifyCompany(company, company.name, newLogoMaterial, company.verified, company.hidden);
    sendMessage(player, "Company logo updated successfully!");
  }
}
