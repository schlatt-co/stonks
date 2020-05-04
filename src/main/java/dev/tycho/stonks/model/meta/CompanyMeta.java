package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.meta.fields.BooleanField;
import uk.tsarcasm.tsorm.Store;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.Field;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import java.util.ArrayList;
import java.util.Collection;

public class CompanyMeta extends EntityMeta<Company> {
  private final Store<CompanyAccount> companyAccountStore;
  private final Store<HoldingsAccount> holdingsAccountStore;
  private final Store<Member> memberStore;
  private final Store<Perk> perkStore;

  public CompanyMeta(Store<CompanyAccount> companyAccountStore, Store<HoldingsAccount> holdingsAccountStore, Store<Member> memberStore, Store<Perk> perkStore) {
    this.companyAccountStore = companyAccountStore;
    this.holdingsAccountStore = holdingsAccountStore;
    this.memberStore = memberStore;
    this.perkStore = perkStore;
    addPk();
    addField("name", new StringField());
    addField("shop_name", new StringField());
    addField("logo_material", new StringField());
    addField("verified", Field.notNull(new BooleanField(false)));
    addField("hidden", Field.notNull(new BooleanField(false)));
  }

  @Override
  protected void getValuesImpl(Company obj) {
    setValue("pk", obj.pk);
    setValue("name", obj.name);
    setValue("shop_name", obj.shopName);
    setValue("logo_material", obj.logoMaterial);
    setValue("verified", obj.verified);
    setValue("hidden", obj.hidden);
  }

  @Override
  protected Company instantiateImpl() {
    int pk = getValue("pk");

    // Get all accounts
    Collection<Account> accounts = new ArrayList<>();
    accounts.addAll(companyAccountStore.getAllWhere(a -> a.companyPk == pk));
    accounts.addAll(holdingsAccountStore.getAllWhere(a -> a.companyPk == pk));
    return new Company(
        pk,
        getValue("name"),
        getValue("shop_name"),
        getValue("logo_material"),
        getValue("verified"),
        getValue("hidden"),
        accounts,
        new ArrayList<>(memberStore.getAllWhere(m -> m.companyPk == pk)),
        new ArrayList<>(perkStore.getAllWhere(p -> p.companyPk == pk))
    );
  }

  @Override
  public Company refreshRelations(Company obj) {
    //Get all accounts
    Collection<Account> accounts = new ArrayList<>();
    accounts.addAll(companyAccountStore.getAllWhere(a -> a.companyPk == obj.pk));
    accounts.addAll(holdingsAccountStore.getAllWhere(a -> a.companyPk == obj.pk));

    return new Company(
        obj.pk,
        obj.name,
        obj.shopName,
        obj.logoMaterial,
        obj.verified,
        obj.hidden,
        accounts,
        new ArrayList<>(memberStore.getAllWhere(m -> m.companyPk == obj.pk)),
        new ArrayList<>(perkStore.getAllWhere(m -> m.companyPk == obj.pk))
    );
  }
}
