package dev.tycho.stonks.model.store;

import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.Collection;
import java.util.UUID;

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
  private ForeignKeyStore<Company, AccountLink> companyAccountLinks;
  private ForeignKeyStore<HoldingsAccount, Holding> accountHoldings;
  private ForeignKeyStore<Company, Service> companyServices;
  private ForeignKeyStore<Service, Subscription> serviceSubscriptions;

  private Repo() {
    instance = this;
    conn = null;
    companyStore = new SyncStore<>(new CompanyDBI(conn));
    accountLinkStore = new SyncStore<>(new AccountLinkDBI(conn));

    companyMembers = new SyncForeignKeyStore<>(companyStore, memberStore, new ForeignKey<>() {
      @Override
      public int getParentPk(Member child) {
        return child.getCompanyPk();
      }

      @Override
      public void createParentReference(Company parent, Collection<Member> children) {
        parent.setMembers(children);
      }

      @Override
      public void createChildReference(Member child, Company parent) {
        child.setCompany(parent);
      }
    });
    companyAccountLinks = new SyncForeignKeyStore<>(companyStore, accountLinkStore, new ForeignKey<>() {
      @Override
      public int getParentPk(AccountLink child) {
        return child.getCompanyPk();
      }

      @Override
      public void createParentReference(Company parent, Collection<AccountLink> children) {
        parent.setAccounts(children);
      }

      @Override
      public void createChildReference(AccountLink child, Company parent) {
        child.setCompany(parent);
      }
    });
    accountHoldings = new SyncForeignKeyStore<>(holdingsAccountStore, holdingStore, new ForeignKey<>() {
      @Override
      public int getParentPk(Holding child) {
        return child.getAccountPk();
      }

      @Override
      public void createChildReference(Holding child, HoldingsAccount parent) {
        child.setAccount(parent);
      }

      @Override
      public void createParentReference(HoldingsAccount parent, Collection<Holding> children) {
        parent.setHoldings(children);
      }
    });
    companyServices = new SyncForeignKeyStore<>(companyStore, serviceStore, new ForeignKey<>() {
      @Override
      public int getParentPk(Service child) {
        return child.getCompanyPk();
      }

      @Override
      public void createParentReference(Company parent, Collection<Service> children) {
        parent.setServices(children);
      }

      @Override
      public void createChildReference(Service child, Company parent) {
        child.setCompany(parent);
      }
    });
    serviceSubscriptions = new SyncForeignKeyStore<>(serviceStore, subscriptionStore, new ForeignKey<>() {
      @Override
      public int getParentPk(Subscription child) {
        return child.getServicePk();
      }

      @Override
      public void createParentReference(Service parent, Collection<Subscription> children) {
        parent.setSubscriptions(children);
      }

      @Override
      public void createChildReference(Subscription child, Service parent) {
        child.setService(parent);
      }
    });





    //Provide an on populate definition here to maintain child collection refrence equality





  }

  //Store getters

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

  public ForeignKeyStore<Company, Service> companyServices() {
    return companyServices;
  }

  public ForeignKeyStore<Service, Subscription> serviceSubscriptions() {
    return serviceSubscriptions;
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
    return Repo.getInstance().companies().getWhere(c -> c.getName().equals(name));
  }

  public ForeignKeyStore<Company, AccountLink> companyAccountLinks() {
    return companyAccountLinks;
  }

  public Account resolveAccountLink(AccountLink accountLink) {
    switch (accountLink.getAccountType()) {
      case "HoldingsAccount":
        return holdingsAccountStore.get(accountLink.getAccountPk());
      case "CompanyAccount":
        return companyAccountStore.get(accountLink.getAccountPk());
      default:
        throw new IllegalArgumentException("AccountLink did not have a recognised account type");
    }
  }

  //todo cache the account link <-> account relation
  public AccountLink accountLinkForAccount(Account account) {
    for (AccountLink link : accountLinkStore.getAll()) {
      if (resolveAccountLink(link).equals(account)) {
        return link;
      }
    }
    return null;
  }

  public Company companyForAccount(Account account) {
    return companyAccountLinks.getParent(accountLinkForAccount(account));
  }

  public Member getCompanyMember(Company company, Player player) {
    for (Member member : companyMembers.getChildren(company)) {
      if (member.getUuid().equals(player.getUniqueId())) {
        return member;
      }
    }
    return null;
  }

  public Boolean hasMember(Company company, Player player) {
    for (Member member : companyMembers.getChildren(company)) {
      if (member.getUuid().equals(player.getUniqueId())) {
        return true;
      }
    }
    return false;
  }


  public HoldingsAccount createHoldingsAccount(Company company, String name, Player player) {
    HoldingsAccount ha = new HoldingsAccount(name, UUID.randomUUID());
    holdingsAccountStore.create(ha);
    //Cache the relation for holdings, which will also populate the account's holding collection
    accountHoldings.putParent(ha);
    //Add a default holding
    accountHoldings.putChild(new Holding(player.getUniqueId(), ha, 1));

    //Create an account link to store the relation to the company
    companyAccountLinks.putChild(new AccountLink(company, ha));
    return ha;
  }

  public CompanyAccount createCompanyAccount(Company company, String name, Player player) {
    CompanyAccount ca = new CompanyAccount(name, UUID.randomUUID());
    companyAccountStore.create(ca);

    //Create an account link to store the relation to the company
    companyAccountLinks.putChild(new AccountLink(company, ca));
    return ca;
  }



}
