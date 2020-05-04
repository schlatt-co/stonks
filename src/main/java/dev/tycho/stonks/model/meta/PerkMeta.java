package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.core.Perk;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

public class PerkMeta extends EntityMeta<Perk> {

  public PerkMeta() {
    addPk();
    addField("company_pk", new IntField());
    addField("namespace", new StringField());
  }

  @Override
  protected void getValuesImpl(Perk obj) {
    setValue("pk", obj.pk);
    setValue("company_pk", obj.companyPk);
    setValue("namespace", obj.namespace);
  }

  @Override
  protected Perk instantiateImpl() {
    return new Perk(
        getValue("pk"),
        getValue("company_pk"),
        getValue("namespace")
    );
  }

  @Override
  public Perk refreshRelations(Perk obj) {
    return obj;
  }
}
