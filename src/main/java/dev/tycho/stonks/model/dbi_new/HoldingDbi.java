package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.dbi_new.fields.UUIDField;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.DoubleField;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;

import javax.sql.DataSource;

public class HoldingDbi extends ModularDbi<Holding> {
    public HoldingDbi(DataSource dataSource) {
        super(dataSource, true);
        addPk();
        addField("player_uuid", new UUIDField());
        addField("account_pk", new IntField());
        addField("balance", new DoubleField());
        addField("share", new DoubleField());

        setupQueryStrings();
    }

    @Override
    protected Holding instantiateSelect() {
        return new Holding(
                getValue("pk"),
                getValue("player_uuid"),
                getValue("account_pk"),
                getValue("balance"),
                getValue("share")
        );
    }

    @Override
    protected Holding instantiateInsert(int pk) {
        return new Holding(
                pk,
                getValue("player_uuid"),
                getValue("account_pk"),
                getValue("balance"),
                getValue("share")
        );
    }

    @Override
    protected void entityToFieldValues(Holding entity) {
        setValue("pk", entity.pk);
        setValue("player_uuid", entity.playerUUID);
        setValue("account_pk", entity.accountPk);
        setValue("balance", entity.balance);
        setValue("share", entity.share);
    }

    @Override
    public Holding refreshRelations(Holding obj) {
        return obj;
    }
}
