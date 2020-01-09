package dev.tycho.stonks.perks;

import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class CompanyChatPerk extends CompanyPerk {

  public CompanyChatPerk(Plugin plugin) {
    super(plugin, "Company Chat", Material.WRITABLE_BOOK, 1000,
        "This perk allows you to talk to all the members in your company!",
        "You can do this by typing /cc then selecting a company",
        "Then type /cc <message> to send a message with that selection",
        "It's like /r but for every online member of the company",
        "Type /cc at any time to switch company you're talking in");
  }

  @Override
  public void onPurchase(Company company, Member purchaser) {

  }
}
