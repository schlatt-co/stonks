package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.core.CompanyAccount;
import dev.tycho.stonks.model.meta.fields.UUIDField;
import dev.tycho.stonks.model.service.Service;
import uk.tsarcasm.tsorm.Store;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.fields.DoubleField;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import java.util.ArrayList;

public class CompanyAccountMeta extends EntityMeta<CompanyAccount> {
  final Store<Service> serviceStore;

  public CompanyAccountMeta(Store<Service> serviceStore) {
    this.serviceStore = serviceStore;
    addPk();
    addField("name", new StringField());
    addField("uuid", new UUIDField());
    addField("company_pk", new IntField());
    addField("balance", new DoubleField());
  }

  @Override
  protected void getValuesImpl(CompanyAccount obj) {
    setValue("pk", obj.pk);
    setValue("uuid", obj.uuid);
    setValue("company_pk", obj.companyPk);
    setValue("balance", obj.balance);
  }

  @Override
  protected CompanyAccount instantiateImpl() {
    int pk = getValue("pk");
    return new CompanyAccount(
        pk,
        getValue("name"),
        getValue("uuid"),
        getValue("company_pk"),
        new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == pk)),
        getValue("balance")
    );
  }

  @Override
  public CompanyAccount refreshRelations(CompanyAccount obj) {
    return new CompanyAccount(
        obj.pk,
        obj.name,
        obj.uuid,
        obj.companyPk,
        new ArrayList<>(serviceStore.getAllWhere(s -> s.accountPk == obj.pk)),
        obj.balance);
  }
}
