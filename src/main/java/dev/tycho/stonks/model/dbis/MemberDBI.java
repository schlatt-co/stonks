package dev.tycho.stonks.model.dbis;

import dev.tycho.stonks.database.JavaSqlDBI;
import dev.tycho.stonks.model.core.Member;
import dev.tycho.stonks.model.core.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MemberDBI extends JavaSqlDBI<Member> {


  public MemberDBI(Connection connection) {
    super(connection);
  }

  @Override
  protected boolean createTable() {
    try {
      connection.createStatement().executeUpdate(
          "CREATE TABLE IF NOT EXISTS member(" +
              "    pk INT(11) NOT NULL AUTO_INCREMENT," +
              "    player_uuid VARCHAR(36) DEFAULT NULL," +
              "    company_pk INT(11) DEFAULT NULL," +
              "    join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ," +
              "    role VARCHAR(20) DEFAULT NULL," +
              "    accepted_invite BIT DEFAULT NULL," +
              "    PRIMARY KEY(pk))"
      );
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Member create(Member obj) {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(
          "INSERT INTO member (player_uuid, company_pk, join_date, role, accepted_invite) VALUES (?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, uuidToStr(obj.playerUUID));
      statement.setInt(2, obj.companyPk);
      statement.setTimestamp(3, obj.joinTimestamp);
      statement.setString(4, obj.role.name());
      statement.setBoolean(5, obj.acceptedInvite);
      statement.executeUpdate();
      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        int newPk = rs.getInt(1);
        return new Member(newPk, obj.playerUUID, obj.companyPk, obj.joinTimestamp, obj.role, obj.acceptedInvite);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Member obj) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "DELETE FROM member WHERE pk = ?");
      statement.setInt(1, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean save(Member obj) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "UPDATE holding SET player_uuid = ?, company_pk = ?, join_date = ?, role = ?, accepted_invite = ? WHERE pk = ?");
      statement.setString(1, uuidToStr(obj.playerUUID));
      statement.setInt(2, obj.companyPk);
      statement.setTimestamp(3, obj.joinTimestamp);
      statement.setString(4, obj.role.name());
      statement.setBoolean(5, obj.acceptedInvite);
      statement.setInt(6, obj.pk);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Member load(int pk) {
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT player_uuid, company_pk, join_date, role, accepted_invite FROM member WHERE pk = ?");
      statement.setInt(1, pk);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        Role newRole;
        try {
          newRole = Role.valueOf(results.getString("role"));
        } catch (IllegalArgumentException e) {
          System.out.println("Error parsing role string");
          e.printStackTrace();
          return null;
        }

        return new Member(
            pk,
            uuidFromString(results.getString("player_uuid")),
            results.getInt("company_pk"),
            results.getTimestamp("join_date"),
            newRole,
            results.getBoolean("accepted_invite"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Member refreshRelations(Member obj) {
    //No foreign relations to update
    return new Member(obj);
  }

  @Override
  public Collection<Member> loadAll() {
    Collection<Member> objects = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT pk, player_uuid, company_pk, join_date, role, accepted_invite FROM member");

      ResultSet results = statement.executeQuery();
      while (results.next()) {
        int pk = results.getInt("pk");
        Role newRole;
        try {
          newRole = Role.valueOf(results.getString("role"));
        } catch (IllegalArgumentException e) {
          System.out.println("Error parsing role string");
          e.printStackTrace();
          return null;
        }

        objects.add(new Member(
            pk,
            uuidFromString(results.getString("player_uuid")),
            results.getInt("company_pk"),
            results.getTimestamp("join_date"),
            newRole,
            results.getBoolean("accepted_invite")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
