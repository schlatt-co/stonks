package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.core.Holding;
import dev.tycho.stonks.model.meta.fields.UUIDField;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.fields.DoubleField;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;

public class HoldingMeta extends EntityMeta<Holding> {
  public HoldingMeta() {
    addPk();
    addField("player_uuid", new UUIDField());
    addField("account_pk", new IntField());
    addField("balance", new DoubleField());
    addField("share", new DoubleField());
  }

  @Override
  protected void getValuesImpl(Holding obj) {
    setValue("pk", obj.pk);
    setValue("player_uuid", obj.playerUUID);
    setValue("account_pk", obj.accountPk);
    setValue("balance", obj.balance);
    setValue("share", obj.share);
  }

  @Override
  protected Holding instantiateImpl() {
    return new Holding(
        getValue("pk"),
        getValue("player_uuid"),
        getValue("account_pk"),
        getValue("balance"),
        getValue("share")
    );
  }

  @Override
  public Holding refreshRelations(Holding obj) {
    return obj;
  }
}
