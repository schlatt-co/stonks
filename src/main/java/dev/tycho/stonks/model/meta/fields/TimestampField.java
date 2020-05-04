package dev.tycho.stonks.model.meta.fields;

import uk.tsarcasm.tsorm.modulardbi.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampField extends Field<Timestamp> {

  public TimestampField() {
    super();
  }

//    public TimestampField(String defaultValue) {
//        super(defaultValue);
//    }

  @Override
  public String getType() {
    return "TIMESTAMP";
  }

  @Override
  protected void setupStatement(int i, PreparedStatement statement, Timestamp value) throws SQLException {
    statement.setTimestamp(i, value);
  }

  @Override
  protected Timestamp getResult(String name, ResultSet results) throws SQLException {
    return results.getTimestamp(name);
  }
}
