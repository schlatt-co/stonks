package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.model.dbi_new.fields.UUIDField;
import dev.tycho.stonks.model.service.Service;
import uk.tsarcasm.tsorm.Store;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import javax.sql.DataSource;
import java.util.ArrayList;

public class HoldingsAccountDbi extends ModularDbi<HoldingsAccount> {
    Store<Service> serviceStore;
    Store<Holding> holdingStore;

    public HoldingsAccountDbi(DataSource dataSource, Store<Service> serviceStore, Store<Holding> holdingStore) {
        super(dataSource, false);

        this.serviceStore = serviceStore;
        this.holdingStore = holdingStore;

        addPk();
        addField("name", new StringField());
        addField("uuid", new UUIDField());
        addField("company_pk", new IntField());

        setupQueryStrings();
    }

    @Override
    protected HoldingsAccount instantiateSelect() {
        int pk = getValue("pk");
        return new HoldingsAccount(
                pk,
                getValue("name"),
                getValue("uuid"),
                getValue("company_pk"),
                new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == pk)),
                new ArrayList<>(holdingStore.getAllWhere(h -> h.accountPk == pk))
                );
    }

    @Override
    protected HoldingsAccount instantiateInsert(int pk) {
        return new HoldingsAccount(
                pk,
                getValue("name"),
                getValue("uuid"),
                getValue("company_pk"),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Override
    protected void entityToFieldValues(HoldingsAccount obj) {
        setValue("pk", obj.pk);
        setValue("uuid", obj.uuid);
        setValue("company_pk", obj.companyPk);
    }

    @Override
    public HoldingsAccount refreshRelations(HoldingsAccount obj) {
        return new HoldingsAccount(
                obj.pk,
                obj.name,
                obj.uuid,
                obj.companyPk,
                new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == obj.pk)),
                new ArrayList<>(holdingStore.getAllWhere(h -> h.accountPk == obj.pk))
        );
    }
}
