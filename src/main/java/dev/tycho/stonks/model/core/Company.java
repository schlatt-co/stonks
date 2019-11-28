package dev.tycho.stonks.model.core;

import com.j256.ormlite.table.DatabaseTable;
import dev.tycho.stonks.database.CompanyDaoImpl;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.store.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

@DatabaseTable(tableName = "company", daoClass = CompanyDaoImpl.class)
public class Company extends Entity {

  private String name;

  private String shopName;

  private Collection<Member> members;

  private Collection<AccountLink> accounts;

  private Collection<Service> services;

  private String logoMaterial;

  private Boolean verified;

  private Boolean hidden;


  public Company(String name, String shopName, String logoMaterial, Boolean verified, Boolean hidden) {
    this.name = name;
    this.shopName = shopName;
    this.logoMaterial = logoMaterial;
    //Companies default to unverified
    this.verified = verified;
    this.hidden = hidden;
  }

  public Member getMember(Player player) {
    for (Member member : members) {
      if (member.getUuid().equals(player.getUniqueId())) {
        return member;
      }
    }
    return null;
  }

  public int getNumAcceptedMembers() {
    int m = 0;
    for (Member member : members) {
      if (member.getAcceptedInvite()) m++;
    }
    return m;
  }

  public double getTotalValue() {
    double totalValue = 0;
    for (AccountLink accountLink : accounts) {
      totalValue += accountLink.getAccount().getTotalBalance();
    }
    return totalValue;
  }

  public Boolean hasMember(Player player) {
    for (Member member : members) {
      if (member.getUuid().equals(player.getUniqueId())) {
        return true;
      }
    }
    return false;
  }

  public Boolean isVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
  }

  public Boolean isHidden() {
    return hidden;
  }

  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getShopName() {
    return shopName;
  }

  public String getLogoMaterial() {
    return logoMaterial;
  }

  public void setLogoMaterial(String logoMaterial) {
    this.logoMaterial = logoMaterial;
  }

  public Collection<Member> getMembers() {
    return members;
  }

  public void setMembers(Collection<Member> members) {
    this.members = members;
  }

  public Collection<AccountLink> getAccounts() {
    return accounts;
  }

  public void setAccounts(Collection<AccountLink> accounts) {
    this.accounts = accounts;
  }

  public Collection<Service> getServices() {
    return services;
  }

  public void setServices(Collection<Service> services) {
    this.services = services;
  }
}
