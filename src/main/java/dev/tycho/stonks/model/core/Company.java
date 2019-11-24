package dev.tycho.stonks.model.core;

import com.j256.ormlite.table.DatabaseTable;
import dev.tycho.stonks.database.CompanyDaoImpl;
import dev.tycho.stonks.model.store.Entity;

@DatabaseTable(tableName = "company", daoClass = CompanyDaoImpl.class)
public class Company extends Entity {

  private String name;

  private String shopName;
//
//  @ForeignCollectionField(eager = true)
//  private ForeignCollection<Member> members;
//
//  @ForeignCollectionField(eager = true)
//  private ForeignCollection<AccountLink> accounts;
//
//  @ForeignCollectionField(eager = true)
//  private ForeignCollection<Service> services;

  private String logoMaterial;

  private Boolean verified;

  private Boolean hidden;


  public Company(String name, String shopName, String logoMaterial, Boolean verified, Boolean hidden) {
    this.name = name;
    this.shopName = shopName;
    this.logoMaterial = logoMaterial;
    //Companies default to unverified
    this.verified = verified;
    this.hidden = hidden;
  }

  public Boolean isVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
  }

  public Boolean isHidden() {
    return hidden;
  }

  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getShopName() {
    return shopName;
  }

  public String getLogoMaterial() {
    return logoMaterial;
  }

  public void setLogoMaterial(String logoMaterial) {
    this.logoMaterial = logoMaterial;
  }

}
