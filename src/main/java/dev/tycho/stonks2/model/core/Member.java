package dev.tycho.stonks2.model.core;

import dev.tycho.stonks2.database.Entity;

import java.sql.Timestamp;
import java.util.UUID;

public class Member extends Entity {

  public final UUID playerUUID;
  public final int companyPk;
  public final Timestamp joinTimestamp;
  public final dev.tycho.stonks2.model.core.Role role;
  public final boolean acceptedInvite;

  public Member(int pk, UUID player, int companyPk, Timestamp joinTimestamp, dev.tycho.stonks2.model.core.Role role, boolean acceptedInvite) {
    super(pk);
    this.playerUUID = player;
    this.companyPk = companyPk;
    this.joinTimestamp = joinTimestamp;
    this.role = role;
    this.acceptedInvite = acceptedInvite;
  }

  public Member(Member member) {
    super(member.pk);
    this.playerUUID = member.playerUUID;
    this.companyPk = member.companyPk;
    this.joinTimestamp = member.joinTimestamp;
    this.role = member.role;
    this.acceptedInvite = member.acceptedInvite;
  }

  public boolean canChangeRole(Member other, dev.tycho.stonks2.model.core.Role newRole) {
    //We are the same role or superior to them
    //So we can change their role
    if (role.compareTo(other.role) <= 0) {
      //We cannot promote them higher than us
      return role.compareTo(newRole) <= 0;
    }
    return false;
  }

  public Boolean hasManagamentPermission() {
    return role == dev.tycho.stonks2.model.core.Role.CEO || role == Role.Manager;
  }
}
