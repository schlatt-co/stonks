package dev.tycho.stonks.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "member", daoClass = MemberDaoImpl.class)
public class Member {

    @DatabaseField(uniqueCombo = true)
    private UUID uuid;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, uniqueCombo = true)
    private Company company;

    @DatabaseField
    private Date joinDate;

    @DatabaseField
    private Role role;

    @DatabaseField
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
        this.company = company;
        this.joinDate = new Date();
        this.acceptedInvite = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Role getRole() {
        return role;
    }

    public Company getCompany() {
        return company;
    }

    public boolean getAcceptedInvite() { return acceptedInvite; }

    public void setAcceptedInvite(boolean acceptedInvite) {
        this.acceptedInvite = acceptedInvite;
    }

    public Boolean hasManagamentPermission() {
        if(this.role.equals(Role.CEO) || this.role.equals(Role.Manager)) {
            return true;
        }
        return false;
    }
}
