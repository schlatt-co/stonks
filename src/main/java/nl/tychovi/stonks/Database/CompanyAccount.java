package nl.tychovi.stonks.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "companyaccount")
public class CompanyAccount {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private UUID uuid;

    @DatabaseField
    private double balance;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Company company;

    @DatabaseField
    private String name;

    public CompanyAccount() {}

    public CompanyAccount(Company company, String name) {
        this.balance = 0;
        this.company = company;
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Company getCompany() {
        return company;
    }

    public void addBalance(double amount) {
        balance += amount;
    }
}
