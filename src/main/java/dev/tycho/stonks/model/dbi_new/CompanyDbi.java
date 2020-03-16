package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.dbi_new.fields.BooleanField;
import uk.tsarcasm.tsorm.Store;
import uk.tsarcasm.tsorm.modulardbi.Field;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;

public class CompanyDbi extends ModularDbi<Company> {
    private final Store<CompanyAccount> companyAccountStore;
    private final Store<HoldingsAccount> holdingsAccountStore;
    private final Store<Member> memberStore;
    private final Store<Perk> perkStore;

    public CompanyDbi(DataSource dataSource, Store<CompanyAccount> companyAccountStore, Store<HoldingsAccount> holdingsAccountStore, Store<Member> memberStore, Store<Perk> perkStore) {
        super(dataSource, false);
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

        setupQueryStrings();
    }

    @Override
    protected Company instantiateSelect() {
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
    protected Company instantiateInsert(int pk) {
        return new Company(
                pk,
                getValue("name"),
                getValue("shop_name"),
                getValue("logo_material"),
                getValue("verified"),
                getValue("hidden"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Override
    protected void entityToFieldValues(Company obj) {
        setValue("pk", obj.pk);
        setValue("name", obj.name);
        setValue("shop_name", obj.shopName);
        setValue("logo_material", obj.logoMaterial);
        setValue("verified", obj.verified);
        setValue("hidden", obj.hidden);
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
