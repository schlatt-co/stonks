package dev.tycho.stonks.model.core;

import dev.tycho.stonks.db_new.Entity;

import java.sql.Date;
import java.util.UUID;

public class Member extends Entity {

    public final UUID playerUUID;
    public final int companyPk;
    public final Date joinDate;
    public final Role role;
    public final boolean acceptedInvite;

    public Member(int pk, UUID player, int companyPk, Date joinDate, Role role, boolean acceptedInvite) {
      super(pk);
      this.playerUUID = player;
      this.companyPk = companyPk;
      this.joinDate = joinDate;
      this.role = role;
      this.acceptedInvite = acceptedInvite;
    }

    public Member(Member member) {
      super(member.pk);
      this.playerUUID = member.playerUUID;
      this.companyPk = member.companyPk;
      this.joinDate = member.joinDate;
      this.role = member.role;
      this.acceptedInvite = member.acceptedInvite;
    }
    public boolean canChangeRole(Member other, Role newRole) {
        //We are the same role or superior to them
        //So we can change their role
        if (role.compareTo(other.role) <= 0) {
            //We cannot promote them higher than us
            return role.compareTo(newRole) <= 0;
        }
        return false;
    }

    public Boolean hasManagamentPermission() {
      return role == Role.CEO || role == Role.Manager;
    }
}
