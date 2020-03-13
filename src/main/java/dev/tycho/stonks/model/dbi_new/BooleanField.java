package dev.tycho.stonks.model.dbi_new;

import uk.tsarcasm.tsorm.modulardbi.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanField extends Field<Boolean> {

    public BooleanField() {
        super();
    }
    public BooleanField(Boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public String getType() {
        return "bit";
    }

    @Override
    protected void setupStatement(int i, PreparedStatement statement, Boolean value) throws SQLException {
        statement.setBoolean(i, value);
    }

    @Override
    protected Boolean getResult(String name, ResultSet results) throws SQLException {
        return results.getBoolean(name);
    }
}
