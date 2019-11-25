package dev.tycho.stonks.model.store;

import dev.tycho.stonks.model.core.*;

import java.sql.Connection;

//The repo has a store for each entity we want to save in the database
public class Repo {
  Connection conn;
  Store<HoldingsAccount> holdingsAccountStore;
  Store<CompanyAccount> companyAccountStore;
  Store<Company> companyStore;
  Store<Holding> holdingStore;
  Store<Member> memberStore;

  OTMStore<Company, Member> companyMembers;
  OTMStore<HoldingsAccount, Holding> accountHoldings;


  public Repo() {
    conn = null;
    companyStore = new SyncStore<>(new CompanyDBI(conn));
    companyMembers = new SyncOTMStore<>(companyStore, memberStore, child -> child.getCompanyPk());
  }


}
