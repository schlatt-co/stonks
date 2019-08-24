package dev.tycho.stonks.dbtest;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "subclass1")
public class Subclass1 extends Superclass {
    @DatabaseField()
    private String customField1;

    public Subclass1(){}

    public String getCustomField1() {
        return customField1;
    }

    public void setCustomField1(String customField1) {
        this.customField1 = customField1;
    }
}
