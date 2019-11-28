package dev.tycho.stonks.model.core;

import dev.tycho.stonks.model.store.Entity;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class Member extends Entity {

  private UUID uuid;

  private int companyPk;
  private Company company;

  private Date joinDate;

  private Role role;

  private boolean acceptedInvite;

  public Member() {

  }

  public Member(Player player, Role role) {
    this.uuid = player.getUniqueId();
    this.role = role;
    this.joinDate = new Date();
    this.acceptedInvite = true;
  }

  public Member(Player player, Role role, Company company) {
    this.uuid = player.getUniqueId();
    this.role = role;
    this.companyPk = company.getPk();
    this.joinDate = new Date();
    this.acceptedInvite = false;
  }

  public UUID getUuid() {
    return uuid;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public boolean canChangeRole(Member other, Role newRole) {
    //We are the same role or superior to them
    //So we can change their role
    if (role.compareTo(other.getRole()) <= 0) {
      //We cannot promote them higher than us
      return role.compareTo(newRole) <= 0;
    }
    return false;
  }

  public int getCompanyPk() {
    return companyPk;
  }

  public boolean getAcceptedInvite() {
    return acceptedInvite;
  }

  public void setAcceptedInvite(boolean acceptedInvite) {
    this.acceptedInvite = acceptedInvite;
  }

  public Boolean hasManagamentPermission() {
    return this.role.equals(Role.CEO) || this.role.equals(Role.Manager);
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }
}
