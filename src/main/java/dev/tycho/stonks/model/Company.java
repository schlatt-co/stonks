package dev.tycho.stonks.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import dev.tycho.stonks.database.CompanyDaoImpl;
import dev.tycho.stonks.managers.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

@DatabaseTable(tableName = "company", daoClass = CompanyDaoImpl.class)
public class Company {

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField()
    private String name;

    @DatabaseField()
    private String shopName;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Member> members;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<AccountLink> accounts;

    @DatabaseField
    private String logoMaterial;

    public Company() {

    }

    public Company(String name, String shopName, Player creator) {
        this.name = name;
        this.shopName = shopName;
        this.logoMaterial = Material.EMERALD.name();
    }

    public UUID getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getShopName() {
        return shopName;
    }

    public String getLogoMaterial() { return logoMaterial; }

    public ForeignCollection<Member> getMembers() {
        return members;
    }

    public Member getMember(Player player) {
        for(Member member : members) {
            if(member.getUuid().equals(player.getUniqueId())) {
                return member;
            }
        }
        return null;
    }

    public int getNumAcceptedMembers() {
        int m = 0;
        for(Member member : members) {
            if (member.getAcceptedInvite()) m++;
        }
        return m;
    }

    public double getTotalValue()
    {
        double totalValue = 0;
        for(AccountLink accountLink : accounts) {
            totalValue += accountLink.getAccount().getTotalBalance();
        }
        return totalValue;
    }

    public void createCompanyAccount(DatabaseManager databaseManager, String name) throws SQLException {
        CompanyAccount companyAccount = new CompanyAccount(name);
        databaseManager.getCompanyAccountDao().create(companyAccount);
        //Create an link entry so the account is registered as ours
        databaseManager.getAccountLinkDao().create(new AccountLink(this, companyAccount));
    }

    public ForeignCollection<AccountLink> getAccounts() {
        return accounts;
    }

    public Boolean hasMember(Player player) {
        for(Member member : members) {
            if(member.getUuid().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void setLogoMaterial(String logoMaterial) {
        this.logoMaterial = logoMaterial;
    }
}
