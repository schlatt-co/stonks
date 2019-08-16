package nl.tychovi.stonks.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "companyaccount")
public class CompanyAccount {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double balance;
}
