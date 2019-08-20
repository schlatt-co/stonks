package nl.tychovi.stonks.Database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import nl.tychovi.stonks.managers.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

@DatabaseTable(tableName = "company", daoClass = CompanyDaoImpl.class)
public class Company {

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField(unique = true)
    private String name;

    @DatabaseField(unique = true)
    private String shopName;

    @ForeignCollectionField()
    private ForeignCollection<Member> members;

    @ForeignCollectionField()
    private ForeignCollection<CompanyAccount> companyAccounts;

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

    public int getTotalValue(DatabaseManager databaseManager) throws SQLException {
        return databaseManager.getCompanyAccountDao().getCompanyValue(this);
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

    public void setLogoMaterial(String logoMaterial) {
        this.logoMaterial = logoMaterial;
    }
}
