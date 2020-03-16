package dev.tycho.stonks.model.dbi_new.fields;

import uk.tsarcasm.tsorm.modulardbi.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDField extends Field<UUID> {

    @Override
    public String getType() {
        return "varchar(36)";
    }

    @Override
    protected void setupStatement(int i, PreparedStatement statement, UUID value) throws SQLException {
        statement.setString(i, value == null ? null : value.toString());

    }

    @Override
    protected UUID getResult(String name, ResultSet results) throws SQLException {
        String str = results.getString(name);
        return str == null ? null : UUID.fromString(str);
    }
}
