package dev.tycho.stonks.model.core;

import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.managers.PerkManager;
import org.bukkit.entity.Player;
import uk.tsarcasm.tsorm.Entity;

import java.util.Collection;
import java.util.UUID;

public class Company extends Entity {

  public final String name;
  public final String shopName;
  public final String logoMaterial;
  public final Boolean verified;
  public final Boolean hidden;

  public final Collection<Member> members;
  public final Collection<Account> accounts;
  public final Collection<Perk> perks;


  public Company(int pk, String name, String shopName, String logoMaterial, Boolean verified, Boolean hidden,
                 Collection<Account> accounts, Collection<Member> members, Collection<Perk> perks) {
    super(pk);
    this.name = name;
    this.shopName = shopName;
    this.members = members;
    this.accounts = accounts;
    this.logoMaterial = logoMaterial;
    this.verified = verified;
    this.hidden = hidden;
    this.perks = perks;
  }

  public Company(Company other) {
    super(other.pk);
    this.name = other.name;
    this.shopName = other.shopName;
    this.members = other.members;
    this.accounts = other.accounts;
    this.logoMaterial = other.logoMaterial;
    this.verified = other.verified;
    this.hidden = other.hidden;
    this.perks = other.perks;
  }

  public Member getMember(Player player) {
    for (Member member : members) {
      if (member.playerUUID.equals(player.getUniqueId())) {
        return member;
      }
    }
    return null;
  }
  public Member getMember(UUID playerUUID) {
    for (Member member : members) {
      if (member.playerUUID.equals(playerUUID)) {
        return member;
      }
    }
    return null;
  }

  public int getNumAcceptedMembers() {
    int m = 0;
    for (Member member : members) {
      if (member.acceptedInvite) m++;
    }
    return m;
  }

  public double getTotalValue() {
    double totalValue = 0;
    for (Account account : accounts) {
      totalValue += account.getTotalBalance();
    }
    return totalValue;
  }

  public Boolean isMember(Player player) {
    for (Member member : members) {
      if (member.playerUUID.equals(player.getUniqueId())) {
        return true;
      }
    }
    return false;
  }

  public boolean ownsPerk(String namespace) {
    for (Perk perk : perks) {
      if (perk.namespace.equalsIgnoreCase(namespace)) {
        return true;
      }
    }
    return false;
  }

  public boolean ownsPerk(Class<? extends CompanyPerk> perk) {
    if (!PerkManager.getInstance().getClassNamespaceMap().containsKey(perk.getName())) {
      return false;
    }
    return ownsPerk(PerkManager.getInstance().getClassNamespaceMap().get(perk.getName()));
  }
}
