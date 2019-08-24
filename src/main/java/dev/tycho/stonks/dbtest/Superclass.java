package dev.tycho.stonks.dbtest;

import com.j256.ormlite.field.DatabaseField;

import java.util.UUID;

public abstract class Superclass {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField()
    private String name;

    public Superclass(){}

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
