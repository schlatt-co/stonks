package dev.tycho.stonks.model.store;

import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.Collection;

//The repo has a store for each entity we want to save in the database
public class Repo {

  private static Repo instance;

  public static Repo getInstance() {
    if (instance == null) {
      new Repo();
    }
    return instance;
  }


  private Connection conn;
  private Store<HoldingsAccount> holdingsAccountStore;
  private Store<CompanyAccount> companyAccountStore;
  private Store<AccountLink> accountLinkStore;
  private Store<Company> companyStore;
  private Store<Holding> holdingStore;
  private Store<Member> memberStore;
  private Store<Service> serviceStore;
  private Store<Subscription> subscriptionStore;

  private ForeignKeyStore<Company, Member> companyMembers;
  private ForeignKeyStore<HoldingsAccount, Holding> accountHoldings;
  private ForeignKeyStore<AccountLink, Service> accountServices;
  private ForeignKeyStore<Service, Subscription> serviceSubscriptions;


  private Repo() {
    instance = this;
    conn = null;
    companyStore = new SyncStore<>(new CompanyDBI(conn));
    accountLinkStore = new SyncStore<>(new AccountLinkDBI(conn));

    companyMembers = new SyncForeignKeyStore<>(companyStore, memberStore, Member::getCompanyPk);
    accountServices = new SyncForeignKeyStore<>(accountLinkStore, serviceStore, Service::getAccountPk);
    serviceSubscriptions = new SyncForeignKeyStore<>(serviceStore, subscriptionStore, Subscription::getService);
  }

  public Store<Company> companies() {
    return companyStore;
  }

  public Store<HoldingsAccount> holdingsAccounts() {
    return holdingsAccountStore;
  }

  public Store<CompanyAccount> companyAccounts() {
    return companyAccountStore;
  }

  public Store<AccountLink> accountLinks() {
    return accountLinkStore;
  }

  public Store<Holding> holdings() {
    return holdingStore;
  }

  public Store<Member> members() {
    return memberStore;
  }

  public Store<Service> services() {
    return serviceStore;
  }

  public Store<Subscription> subscriptions() {
    return subscriptionStore;
  }

  public ForeignKeyStore<Company, Member> companyMembers() {
    return companyMembers;
  }


  public Collection<Company> companiesWhereManager(Player player) {
    Collection<Company> list = Repo.getInstance().companies().getAll();
    list.removeIf(c -> {
      for (Member m : Repo.getInstance().companyMembers().getChildren(c)) {
        if (m.getUuid().equals(player.getUniqueId())) {
          //If a manager then keep
          if (m.hasManagamentPermission()) return false;
        }
      }
      return true;
    });
    return list;
  }

  public Company companyWithName(String name) {
    return Repo.getInstance().companies().getWhere(c->c.getName().equals(name));
  }


}
