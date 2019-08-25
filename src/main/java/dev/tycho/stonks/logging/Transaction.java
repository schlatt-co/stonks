package dev.tycho.stonks.logging;

import com.Acrobot.ChestShop.ORMlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import dev.tycho.stonks.model.AccountLink;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

@DatabaseTable (tableName = "transaction")
public class Transaction {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private AccountLink account;

    @DatabaseField()
    private UUID payee = null;

    //negative amount represents money withdrawn
    @DatabaseField()
    private double amount;

    @DatabaseField()
    private Timestamp timestamp;

    public Transaction() {
    }
    public Transaction(AccountLink account, double amount ) {
        this.account = account;
        this.payee = null;
        this.amount = amount;
        this.timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
    }
    public Transaction(AccountLink account, UUID payee, double amount ) {
        this.account = account;
        this.payee = payee;
        this.amount = amount;
        this.timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
    }
}
