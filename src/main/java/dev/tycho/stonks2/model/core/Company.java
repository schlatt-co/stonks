package dev.tycho.stonks2.model.core;

import dev.tycho.stonks2.database.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class Company extends Entity {

  public final String name;
  public final String shopName;
  public final String logoMaterial;
  public final Boolean verified;
  public final Boolean hidden;

  public final Collection<dev.tycho.stonks2.model.core.Member> members;

  public final Collection<dev.tycho.stonks2.model.core.Account> accounts;


  public Company(int pk, String name, String shopName, String logoMaterial, Boolean verified, Boolean hidden,
                 Collection<dev.tycho.stonks2.model.core.Account> accounts, Collection<dev.tycho.stonks2.model.core.Member> members) {
    super(pk);
    this.name = name;
    this.shopName = shopName;
    this.members = members;
    this.accounts = accounts;
    this.logoMaterial = logoMaterial;
    this.verified = verified;
    this.hidden = hidden;
  }

  public Company(Company company) {
    super(company.pk);
    this.name = company.name;
    this.shopName = company.shopName;
    this.members = new ArrayList<>(company.members);
    this.accounts = new ArrayList<>(company.accounts);
    this.logoMaterial = company.logoMaterial;
    this.verified = company.verified;
    this.hidden = company.hidden;
  }

  public dev.tycho.stonks2.model.core.Member getMember(Player player) {
    for (dev.tycho.stonks2.model.core.Member member : members) {
      if (member.playerUUID.equals(player.getUniqueId())) {
        return member;
      }
    }
    return null;
  }

  public int getNumAcceptedMembers() {
    int m = 0;
    for (dev.tycho.stonks2.model.core.Member member : members) {
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
}
