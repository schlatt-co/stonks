package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import uk.tsarcasm.tsorm.Store;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.DoubleField;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import javax.sql.DataSource;
import java.util.ArrayList;

public class ServiceDbi extends ModularDbi<Service> {

    Store<Subscription> subscriptionStore;

    public ServiceDbi(DataSource dataSource, Store<Subscription> subscriptionStore) {
        super(dataSource, true);
        this.subscriptionStore = subscriptionStore;

        addPk();
        addField("name", new StringField());
        addField("duration", new DoubleField());
        addField("cost", new DoubleField());
        addField("max_subscribers", new IntField());
        addField("account_pk", new IntField());

        setupQueryStrings();
    }

    @Override
    protected Service instantiateSelect() {
        int pk = getValue("pk");
        return new Service(
                pk,
                getValue("name"),
                getValue("duration"),
                getValue("cost"),
                getValue("max_subscribers"),
                getValue("account_pk"),
                new ArrayList<>(subscriptionStore.getAllWhere(s -> s.servicePk == pk))
        );
    }

    @Override
    protected Service instantiateInsert(int pk) {
        return new Service(
                pk,
                getValue("name"),
                getValue("duration"),
                getValue("cost"),
                getValue("max_subscribers"),
                getValue("account_pk"),
                new ArrayList<>()
        );
    }

    @Override
    protected void entityToFieldValues(Service entity) {
        setValue("pk", entity.pk);
        setValue("name", entity.name);
        setValue("duration", entity.duration);
        setValue("cost", entity.cost);
        setValue("max_subscribers", entity.maxSubscribers);
        setValue("account_pk", entity.accountPk);
    }

    @Override
    public Service refreshRelations(Service obj) {
        return new Service(
                obj.pk,
                obj.name,
                obj.duration,
                obj.cost,
                obj.maxSubscribers,
                obj.accountPk,
                new ArrayList<>(subscriptionStore.getAllWhere(s -> s.servicePk == obj.pk))
        );
    }
}
