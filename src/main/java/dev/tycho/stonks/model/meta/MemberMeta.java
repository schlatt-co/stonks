package dev.tycho.stonks.model.meta;

import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.core.Role;
import dev.tycho.stonks.model.meta.fields.BooleanField;
import dev.tycho.stonks.model.meta.fields.TimestampField;
import dev.tycho.stonks.model.meta.fields.UUIDField;
import uk.tsarcasm.tsorm.modulardbi.EntityMeta;
import uk.tsarcasm.tsorm.modulardbi.Field;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberMeta extends EntityMeta<Member> {
  public MemberMeta() {
    addPk();
    addField("player_uuid", new UUIDField());
    addField("company_pk", new IntField());
    // todo join_date needs a default value
    addField("join_date", new TimestampField());
    addField("role", new Field<Role>() {
      @Override
      public String getType() {
        return "varchar(20)";
      }

      @Override
      protected void setupStatement(int i, PreparedStatement statement, Role value) throws SQLException {
        statement.setString(i, value.name());
      }

      @Override
      protected Role getResult(String name, ResultSet results) throws SQLException {
        try {
          return Role.valueOf(results.getString(name));
        } catch (IllegalArgumentException e) {
          System.out.println("Error parsing role string");
          e.printStackTrace();
          return null;
        }
      }
    });
    addField("accepted_invite", Field.notNull(new BooleanField(false)));
  }

  @Override
  protected void getValuesImpl(Member obj) {
    setValue("pk", obj.pk);
    setValue("player_uuid", obj.playerUUID);
    setValue("company_pk", obj.companyPk);
    setValue("join_date", obj.joinTimestamp);
    setValue("role", obj.role);
    setValue("accepted_invite", obj.acceptedInvite);
  }

  @Override
  protected Member instantiateImpl() {
    return new Member(
        getValue("pk"),
        getValue("player_uuid"),
        getValue("company_pk"),
        getValue("join_date"),
        getValue("role"),
        getValue("accepted_invite")
    );
  }

  @Override
  public Member refreshRelations(Member obj) {
    return obj;
  }
}
