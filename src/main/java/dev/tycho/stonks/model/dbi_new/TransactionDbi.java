package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.dbi_new.fields.TimestampField;
import dev.tycho.stonks.model.dbi_new.fields.UUIDField;
import dev.tycho.stonks.model.logging.Transaction;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.DoubleField;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import javax.sql.DataSource;

public class TransactionDbi extends ModularDbi<Transaction> {
    public TransactionDbi(DataSource dataSource) {
        super(dataSource, true);

        addPk();
        addField("account_pk", new IntField());
        addField("payee_uuid", new UUIDField());
        addField("message", new StringField());
        addField("amount", new DoubleField());
        //todo default timestamp
        addField("timestamp", new TimestampField());

        setupQueryStrings();
    }

    @Override
    protected Transaction instantiateSelect() {
        return instantiateInsert(getValue("pk"));
    }

    @Override
    protected Transaction instantiateInsert(int pk) {
        return new Transaction(
                pk,
                getValue("account_pk"),
                getValue("payee_uuid"),
                getValue("message"),
                getValue("amount"),
                getValue("timestamp")
        );
    }

    @Override
    protected void entityToFieldValues(Transaction entity) {
        setValue("pk", entity.pk);
        setValue("account_pk", entity.accountPk);
        setValue("payee_uuid", entity.payeeUUID);
        setValue("message", entity.message);
        setValue("amount", entity.amount);
        setValue("timestamp", entity.timestamp);
    }

    @Override
    public Transaction refreshRelations(Transaction obj) {
        return obj;
    }
}
