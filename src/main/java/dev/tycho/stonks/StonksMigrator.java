package dev.tycho.stonks;

import java.sql.*;
import java.util.UUID;

public class StonksMigrator {

  @SuppressWarnings("DuplicatedCode")
  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection oldDb = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/migrate?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT", "root", "password");
    Connection newDb = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/stonks?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT", "root", "password");
    ResultSet companies = oldDb.createStatement().executeQuery("select * from company;");
    int curID = 1;
    while (companies.next()) {
      UUID companyUuid = UUID.fromString(companies.getString("id"));
      PreparedStatement statement = newDb.prepareStatement("INSERT INTO company (pk, name, shop_name, logo_material, verified, hidden) VALUES (?, ?, ?, ?, ?, ?);");
      statement.setInt(1, curID);
      statement.setString(2, companies.getString("name"));
      statement.setString(3, companies.getString("shopName"));
      statement.setString(4, companies.getString("logoMaterial"));
      statement.setBoolean(5, companies.getBoolean("verified"));
      statement.setBoolean(6, companies.getBoolean("hidden"));
      statement.executeUpdate();
      statement = oldDb.prepareStatement("select * from accountlink where company_id = ?");
      statement.setString(1, companyUuid.toString());
      ResultSet accounts = statement.executeQuery();
      while (accounts.next()) {
        Integer accountId = getInteger(accounts, "companyAccount_id");
        if (accountId != null) {
          ResultSet accountInfo = oldDb.prepareStatement("select * from companyaccount where id = " + accountId + ";").executeQuery();
          if (!accountInfo.next()) {
            System.out.println("Invalid account id!");
            continue;
          }
          statement = newDb.prepareStatement("insert into company_account (pk, name, uuid, company_pk, balance) values (?, ?, ?, ?, ?);");
          statement.setInt(1, accountId);
          statement.setString(2, accountInfo.getString("name"));
          statement.setString(3, accountInfo.getString("uuid"));
          statement.setInt(4, curID);
          statement.setDouble(5, 0);
          statement.executeUpdate();
          continue;
        }
        accountId = getInteger(accounts, "holdingsAccount_id");
        if (accountId != null) {
          ResultSet accountInfo = oldDb.prepareStatement("select * from holdingsaccount where id = " + accountId + ";").executeQuery();
          if (!accountInfo.next()) {
            System.out.println("Invalid account id!");
            continue;
          }
          statement = newDb.prepareStatement("insert into holdings_account (pk, name, uuid, company_pk) values (?, ?, ?, ?);");
          statement.setInt(1, accountId);
          statement.setString(2, accountInfo.getString("name"));
          statement.setString(3, accountInfo.getString("uuid"));
          statement.setInt(4, curID);
          statement.executeUpdate();
          ResultSet holdings = oldDb.createStatement().executeQuery("select * from holding where holdingsAccount_id = " + accountId + ";");
          while (holdings.next()) {
            statement = newDb.prepareStatement("insert into holding (player_uuid, balance, share, account_pk) values (?, ?, ?, ?);");
            statement.setString(1, holdings.getString("player"));
            statement.setDouble(2, holdings.getDouble("balance"));
            statement.setDouble(3, holdings.getDouble("share"));
            statement.setInt(4, accountId);
            statement.executeUpdate();
          }
          continue;
        }
        System.out.println("Invalid account link!");
      }

      //members
      statement = oldDb.prepareStatement("select * from member where company_id = ?");
      statement.setString(1, companyUuid.toString());
      ResultSet members = statement.executeQuery();
      while (members.next()) {
        statement = newDb.prepareStatement("insert into member (player_uuid, company_pk, join_date, role, accepted_invite) values (?, ?, ?, ?, ?);");
        statement.setString(1, members.getString("uuid"));
        statement.setInt(2, curID);
        statement.setTimestamp(3, members.getTimestamp("joinDate"));
        statement.setString(4, members.getString("role"));
        statement.setBoolean(5, members.getBoolean("acceptedInvite"));
        statement.executeUpdate();
      }
      curID++;
    }
    System.out.println("Migrated!");
  }

  private static Integer getInteger(ResultSet rs, String strColName) throws SQLException {
    int nValue = rs.getInt(strColName);
    return rs.wasNull() ? null : nValue;
  }
}
