package nl.tychovi.stonks.Database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import nl.tychovi.stonks.managers.DatabaseManager;
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

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Member> members;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<CompanyAccount> companyAccounts;

    public Company() {

    }

    public Company(String name, String shopName, Player creator) {
        this.name = name;
        this.shopName = shopName;
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
}
