package dev.tycho.stonks.model.dbi_new;

import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.core.Role;
import dev.tycho.stonks.model.dbi_new.fields.BooleanField;
import dev.tycho.stonks.model.dbi_new.fields.TimestampField;
import dev.tycho.stonks.model.dbi_new.fields.UUIDField;
import uk.tsarcasm.tsorm.modulardbi.Field;
import uk.tsarcasm.tsorm.modulardbi.ModularDbi;
import uk.tsarcasm.tsorm.modulardbi.fields.IntField;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDbi extends ModularDbi<Member> {
    public MemberDbi(DataSource dataSource) {
        super(dataSource, true);
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

        setupQueryStrings();
    }

    @Override
    protected Member instantiateSelect() {
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
    protected Member instantiateInsert(int pk) {
        return new Member(
                pk,
                getValue("player_uuid"),
                getValue("company_pk"),
                getValue("join_date"),
                getValue("role"),
                getValue("accepted_invite")
        );
    }

    @Override
    protected void entityToFieldValues(Member entity) {
        setValue("pk", entity.pk);
        setValue("player_uuid", entity.playerUUID);
        setValue("company_pk", entity.companyPk);
        setValue("join_date", entity.joinTimestamp);
        setValue("role", entity.role);
        setValue("accepted_invite", entity.acceptedInvite);
    }

    @Override
    public Member refreshRelations(Member obj) {
        return obj;
    }
}
