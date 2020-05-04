package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import uk.tsarcasm.tsorm.Store;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.fields.DoubleField;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import java.util.ArrayList;

public class ServiceMeta extends EntityMeta<Service> {

  Store<Subscription> subscriptionStore;

  public ServiceMeta(Store<Subscription> subscriptionStore) {
    this.subscriptionStore = subscriptionStore;

    addPk();
    addField("name", new StringField());
    addField("duration", new DoubleField());
    addField("cost", new DoubleField());
    addField("max_subscribers", new IntField());
    addField("account_pk", new IntField());
  }

  @Override
  protected void getValuesImpl(Service obj) {
    setValue("pk", obj.pk);
    setValue("name", obj.name);
    setValue("duration", obj.duration);
    setValue("cost", obj.cost);
    setValue("max_subscribers", obj.maxSubscribers);
    setValue("account_pk", obj.accountPk);
  }

  @Override
  protected Service instantiateImpl() {
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
