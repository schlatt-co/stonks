package nl.tychovi.stonks.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "company")
public class Company {

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String shopName;

    public Company() {

    }

    public Company(String name, String shopName) {
        this.name = name;
        this.shopName = shopName;
    }
}
