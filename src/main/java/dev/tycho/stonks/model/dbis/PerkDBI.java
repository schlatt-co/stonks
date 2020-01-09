package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.model.core.Perk;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class PerkDBI extends JavaSqlDBI<Perk> {

  public PerkDBI(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected boolean createTable() {
    try {
      try (Connection conn = getConnection()) {
        conn.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS perk(" +
                "    pk INT(11) NOT NULL AUTO_INCREMENT," +
                "    company_pk INT(11) DEFAULT NULL," +
                "    namespace VARCHAR(255) DEFAULT NULL," +
                "    PRIMARY KEY(pk))"
        );
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Perk create(Perk obj) {
    PreparedStatement statement;
    try {
      try (Connection conn = getConnection()) {
        statement = conn.prepareStatement(
            "INSERT INTO perk (company_pk, namespace) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, obj.companyPk);
        statement.setString(2, obj.namespace);
        statement.executeUpdate();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          int newPk = rs.getInt(1);
          return new Perk(newPk, obj.companyPk, obj.namespace);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Perk obj) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "DELETE FROM perk WHERE pk = ?");
        statement.setInt(1, obj.pk);
        statement.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean save(Perk obj) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "UPDATE perk SET company_pk = ?, namespace = ? WHERE pk = ?");
        statement.setInt(1, obj.companyPk);
        statement.setString(2, obj.namespace);
        statement.setInt(3, obj.pk);
        statement.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Perk load(int pk) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT company_pk, namespace FROM perk WHERE pk = ?");
        statement.setInt(1, pk);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
          return new Perk(
              pk,
              results.getInt("company_pk"),
              results.getString("namespace"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Perk refreshRelations(Perk obj) {
    return obj;
  }

  @Override
  public Collection<Perk> loadAll() {
    Collection<Perk> objects = new ArrayList<>();
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT pk, company_pk, namespace FROM perk");

        ResultSet results = statement.executeQuery();
        while (results.next()) {
          int pk = results.getInt("pk");
          objects.add(new Perk(
              pk,
              results.getInt("company_pk"),
              results.getString("namespace")));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
