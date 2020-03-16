package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.core.Perk;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

import javax.sql.DataSource;

public class PerkDbi extends ModularDbi<Perk> {

    public PerkDbi(DataSource dataSource) {
        super(dataSource, true);
        addPk();
        addField("company_pk", new IntField());
        addField("namespace", new StringField());

        setupQueryStrings();
    }

    @Override
    protected Perk instantiateSelect() {
        return new Perk(
                getValue("pk"),
                getValue("company_pk"),
                getValue("namespace")
        );
    }

    @Override
    protected Perk instantiateInsert(int pk) {
        return new Perk(pk,
                getValue("company_pk"),
                getValue("namespace")
        );
    }

    @Override
    protected void entityToFieldValues(Perk entity) {
        setValue("pk", entity.pk);
        setValue("company_pk", entity.companyPk);
        setValue("namespace", entity.namespace);
    }

    @Override
    public Perk refreshRelations(Perk obj) {
        return obj;
    }
}
