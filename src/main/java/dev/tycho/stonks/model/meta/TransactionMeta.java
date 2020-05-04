package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.meta.fields.TimestampField;
import dev.tycho.stonks.model.meta.fields.UUIDField;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.fields.DoubleField;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;
import uk.tsarcasm.tsorm.modulardbi.fields.StringField;

public class TransactionMeta extends EntityMeta<Transaction> {
  public TransactionMeta() {
    addPk();
    addField("account_pk", new IntField());
    addField("payee_uuid", new UUIDField());
    addField("message", new StringField());
    addField("amount", new DoubleField());
    //todo default timestamp
    addField("timestamp", new TimestampField());
  }

  @Override
  protected void getValuesImpl(Transaction obj) {
    setValue("pk", obj.pk);
    setValue("account_pk", obj.accountPk);
    setValue("payee_uuid", obj.payeeUUID);
    setValue("message", obj.message);
    setValue("amount", obj.amount);
    setValue("timestamp", obj.timestamp);
  }

  @Override
  protected Transaction instantiateImpl() {
    return new Transaction(
        getValue("pk"),
        getValue("account_pk"),
        getValue("payee_uuid"),
        getValue("message"),
        getValue("amount"),
        getValue("timestamp")
    );
  }

  @Override
  public Transaction refreshRelations(Transaction obj) {
    return obj;
  }
}
