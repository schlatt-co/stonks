package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.core.HoldingsAccount;
import dev.tycho.stonks.model.meta.fields.UUIDField;
import dev.tycho.stonks.model.service.Service;
import uk.tsarcasm.tsorm.Store;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import java.util.ArrayList;

public class HoldingsAccountMeta extends EntityMeta<HoldingsAccount> {
  Store<Service> serviceStore;
  Store<Holding> holdingStore;

  public HoldingsAccountMeta(Store<Service> serviceStore, Store<Holding> holdingStore) {
    this.serviceStore = serviceStore;
    this.holdingStore = holdingStore;

    addPk();
    addField("name", new StringField());
    addField("uuid", new UUIDField());
    addField("company_pk", new IntField());
  }

  @Override
  protected void getValuesImpl(HoldingsAccount obj) {
    setValue("pk", obj.pk);
    setValue("uuid", obj.uuid);
    setValue("company_pk", obj.companyPk);
  }

  @Override
  protected HoldingsAccount instantiateImpl() {
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
