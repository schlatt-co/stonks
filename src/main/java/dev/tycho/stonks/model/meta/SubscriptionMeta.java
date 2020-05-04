package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.meta.fields.BooleanField;
import dev.tycho.stonks.model.meta.fields.TimestampField;
import dev.tycho.stonks.model.meta.fields.UUIDField;
import dev.tycho.stonks.model.service.Subscription;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.Field;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;

public class SubscriptionMeta extends EntityMeta<Subscription> {
  public SubscriptionMeta() {
    addPk();
    addField("player_uuid", new UUIDField());
    addField("service_pk", new IntField());
    addField("last_payment_date", new TimestampField());
    addField("auto_pay", Field.notNull(new BooleanField(true)));
  }

  @Override
  protected void getValuesImpl(Subscription obj) {
    setValue("pk", obj.pk);
    setValue("player_uuid", obj.playerUUID);
    setValue("service_pk", obj.servicePk);
    setValue("last_payment_date", obj.lastPaymentTimestamp);
    setValue("auto_pay", obj.lastPaymentTimestamp);
  }

  @Override
  protected Subscription instantiateImpl() {
    return new Subscription(
        getValue("pk"),
        getValue("player_uuid"),
        getValue("service_pk"),
        getValue("last_payment_date"),
        getValue("auto_pay")
    );
  }

  @Override
  public Subscription refreshRelations(Subscription obj) {
    return obj;
  }
}
