package dev.tycho.stonks.gui;

import dev.tycho.stonks.api.StonksAPI;
import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.managers.PerkManager;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PerkListGui extends CollectionGui<CompanyPerk> {

  private Company company;

  public PerkListGui(Company company) {
    super(PerkManager.getInstance().getRegisteredPerks().values(), company.name + "'s Perks");
    this.company = company;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to info"),
        e -> player.performCommand("stonks info " + company.name)));
    contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.logoMaterial),
        company.name)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, CompanyPerk obj) {
    return ClickableItem.of(ItemInfoHelper.perkDisplayItem(company, obj), e -> {
      Member member = company.getMember(player);
      if (company.ownsPerk(obj.getNamespace())) {
        new PerkActionListGui(company, obj)
            .show(player);
      } else if (member == null || !member.hasManagamentPermission()) {
        sendMessage(player, "You have insufficient permissions to purchase perks for this company!");
        getInventory().close(player);
      } else if (obj.isVerifiedOnly() && !company.verified) {
        sendMessage(player, "This perk is for verified companies only! Please have you company verified in order to purchase this perk!");
        getInventory().close(player);
      } else {
        new AccountSelectorGui.Builder()
            .company(company)
            .title("Select account to purchase with")
            .accountSelected(acc -> purchasePerk(player, acc, obj))
            .show(player);
      }
    });
  }

  protected void purchasePerk(Player player, Account account, CompanyPerk perk) {
    Company company = Repo.getInstance().companies().get(account.companyPk);
    IAccountVisitor visitor = new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        if (a.getTotalBalance() < perk.getPrice()) {
          sendMessage(player, "That account doesn't have enough funds to complete this transaction!");
          return;
        }

        Repo.getInstance().withdrawFromAccount(player.getUniqueId(), a, perk.getPrice());
        PerkManager.getInstance().awardPerk(company, perk, company.getMember(player));
        if (StonksAPI.getCompany("Admins") != null) {
          //noinspection OptionalGetWithoutIsPresent
          Repo.getInstance().payAccount(player.getUniqueId(), "Perk Purchase for " + company.name, Objects.requireNonNull(StonksAPI.getCompany("Admins")).accounts.stream().filter(p -> p.name.equals("Main")).findFirst().get(), perk.getPrice());
        }
        sendMessage(player, "Perk purchased successfully!");
        Repo.getInstance().sendMessageToAllOnlineMembers(company, player.getName() + " has purchased the " + perk.getName() + " perk for the company: " + company.name);
      }

      @Override
      public void visit(HoldingsAccount a) {
        sendMessage(player, "Holding accounts cannot be used to purchase perks!");
      }
    };
    account.accept(visitor);
  }
}
