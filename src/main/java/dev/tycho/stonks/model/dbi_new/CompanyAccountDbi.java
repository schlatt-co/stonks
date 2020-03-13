package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.core.CompanyAccount;
import dev.tycho.stonks.model.service.Service;
import uk.tsarcasm.tsorm.Store;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.DoubleField;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import javax.sql.DataSource;
import java.util.ArrayList;

public class CompanyAccountDbi extends ModularDbi<CompanyAccount> {
    Store<Service> serviceStore;

    public CompanyAccountDbi(DataSource dataSource) {
        super(dataSource, false);
        addPk();
        addField("name", new StringField());
        addField("uuid", new UUIDField());
        addField("company_pk", new IntField());
        addField("balance", new DoubleField());

        setupQueryStrings();
    }

    @Override
    protected CompanyAccount instantiateSelect() {
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
    protected CompanyAccount instantiateInsert(int pk) {
        return new CompanyAccount(
                pk,
                getValue("name"),
                getValue("uuid"),
                getValue("company_pk"),
                new ArrayList<>(),
                getValue("balance")
        );
    }

    @Override
    protected void entityToFieldValues(CompanyAccount obj) {
        setValue("pk", obj.pk);
        setValue("uuid", obj.uuid);
        setValue("company_pk", obj.companyPk);
        setValue("balance", obj.balance);
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
