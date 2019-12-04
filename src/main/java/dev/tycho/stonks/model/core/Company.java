package dev.tycho.stonks.model.core;

import dev.tycho.stonks.db_new.Entity;
import dev.tycho.stonks.model.service.Service;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class Company extends Entity {

  public final String name;

  public final String shopName;

  public final Collection<Member> members;

  public final Collection<Account> accounts;

  public final Collection<Service> services;

  public final String logoMaterial;

  public final Boolean verified;

  public final Boolean hidden;


  public Company(int pk, String name, String shopName, Collection<Member> members, Collection<Account> accounts,
                 Collection<Service> services, String logoMaterial, Boolean verified, Boolean hidden) {
    super(pk);
    this.name = name;
    this.shopName = shopName;
    this.members = members;
    this.accounts = accounts;
    this.services = services;
    this.logoMaterial = logoMaterial;
    //Companies default to unverified
    this.verified = verified;
    this.hidden = hidden;
  }

  public Company(Company company) {
    super(company.pk);
    name = company.name;
    shopName = company.shopName;
    members = new ArrayList<>(company.members);
    accounts = new ArrayList<>(company.accounts);
    services = new ArrayList<>(company.services);
    logoMaterial = company.logoMaterial;
    verified = company.verified;
    hidden = company.hidden;
  }

  public Member getMember(Player player) {
    for (Member member : members) {
      if (member.uuid.equals(player.getUniqueId())) {
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
      if (member.uuid.equals(player.getUniqueId())) {
        return true;
      }
    }
    return false;
  }
}
