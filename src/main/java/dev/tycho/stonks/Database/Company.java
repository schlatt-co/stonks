package dev.tycho.stonks.Database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import dev.tycho.stonks.managers.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "company", daoClass = CompanyDaoImpl.class)
public class Company {

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField(unique = true)
    private String name;

    @DatabaseField(unique = true)
    private String shopName;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Member> members;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<CompanyAccount> companyAccounts;

    @DatabaseField
    private String logoMaterial;

    private double totalValue;

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

    public double getTotalValue() {
        return totalValue;
    }

    public void createCompanyAccount(DatabaseManager databaseManager, String name) throws SQLException {
        CompanyAccount companyAccount = new CompanyAccount(this, name);
        databaseManager.getCompanyAccountDao().create(companyAccount);
    }

    public ForeignCollection<CompanyAccount> getCompanyAccounts() {
        return companyAccounts;
    }

    public Boolean hasMember(Player player) {
        for(Member member : members) {
            if(member.getUuid().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void calculateTotalValue(){
        double totalValue = 0;
        for(CompanyAccount companyAccount : companyAccounts) {
            totalValue += companyAccount.getBalance();
        }
        this.totalValue = totalValue;

    }

    public void setLogoMaterial(String logoMaterial) {
        this.logoMaterial = logoMaterial;
    }
}
