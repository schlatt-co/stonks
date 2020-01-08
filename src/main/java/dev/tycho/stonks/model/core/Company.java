package dev.tycho.stonks.model.core;

import dev.tycho.stonks.database.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

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

  public Member getMember(Player player) {
    for (Member member : members) {
      if (member.playerUUID.equals(player.getUniqueId())) {
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
}
