package dev.tycho.stonks.model.core;

import com.j256.ormlite.field.DatabaseField;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;

import java.util.UUID;

public abstract class Account {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private UUID uuid;

    @DatabaseField
    private String name;

    public Account() {}

    public Account(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public int getId() {return id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public abstract void addBalance(double amount);
    public abstract double getTotalBalance();
    public abstract void accept(IAccountVisitor visitor);


}
