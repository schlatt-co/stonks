package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.dbi_new.fields.BooleanField;
import dev.tycho.stonks.model.dbi_new.fields.TimestampField;
import dev.tycho.stonks.model.dbi_new.fields.UUIDField;
import dev.tycho.stonks.model.service.Subscription;
import uk.tsarcasm.tsorm.modulardbi.Field;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.UUID;

public class SubscriptionDbi extends ModularDbi<Subscription> {
    public SubscriptionDbi(DataSource dataSource) {
        super(dataSource, true);

        addPk();
        addField("player_uuid", new UUIDField());
        addField("service_pk", new IntField());
        addField("last_payment_date", new TimestampField());
        addField("auto_pay", Field.notNull(new BooleanField(true)));

        setupQueryStrings();
    }

    @Override
    protected Subscription instantiateSelect() {
        return new Subscription(
                getValue("pk"),
                getValue("player_uuid"),
                getValue("service_pk"),
                getValue("last_payment_date"),
                getValue("auto_pay")
        );
    }

    @Override
    protected Subscription instantiateInsert(int pk) {
        return new Subscription(
                pk,
                getValue("player_uuid"),
                getValue("service_pk"),
                getValue("last_payment_date"),
                getValue("auto_pay")
        );
    }

    @Override
    protected void entityToFieldValues(Subscription entity) {
        setValue("pk", entity.pk);
        setValue("player_uuid", entity.playerUUID);
        setValue("service_pk", entity.servicePk);
        setValue("last_payment_date", entity.lastPaymentTimestamp);
        setValue("auto_pay", entity.lastPaymentTimestamp);
    }

    @Override
    public Subscription refreshRelations(Subscription obj) {
        return obj;
    }
}
