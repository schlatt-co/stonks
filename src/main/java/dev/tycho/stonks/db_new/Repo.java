package dev.tycho.stonks.db_new;

import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.Material;
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
  private Store<Company> companyStore;
  private Store<CompanyAccount> companyAccountStore;
  private Store<HoldingsAccount> holdingsAccountStore;
  private Store<Holding> holdingStore;
  private Store<Member> memberStore;
  private Store<Service> serviceStore;
  private Store<Subscription> subscriptionStore;

  private Repo() {
    instance = this;
    conn = null;
    companyStore = new SyncStore<>(new CompanyDBI(conn), Company::new);
    companyAccountStore = new SyncStore<>(null, CompanyAccount::new);
    holdingsAccountStore = new SyncStore<>(null, HoldingsAccount::new);
    holdingStore = new SyncStore<>(null, Holding::new);
    memberStore = new SyncStore<>(null, Member::new);
    serviceStore = new SyncStore<>(null, Service::new);
    subscriptionStore = new SyncStore<>(null, Subscription::new);

  }


  public Collection<Company> companiesWhereManager(Player player) {
    Collection<Company> list = Repo.getInstance().companies().getAll();
    list.removeIf(c -> {
      for (Member m : c.members) {
        //If a manager then keep
        if (m.uuid.equals(player.getUniqueId()) && m.hasManagamentPermission()) return false;
      }
      return true;
    });
    return list;
  }

  public Company companyWithName(String name) {
    return Repo.getInstance().companies().getWhere(c -> c.name.equals(name));
  }

  public Company createCompany(String companyName, Player player) {
    Company c = new Company(0, companyName, "S" + companyName,
        null, null, null,
        Material.EMERALD.name(), false, false);
    c = companyStore.create(c);

    //todo add current date here
    Member ceo = new Member(0, player.getUniqueId(), c.pk, null, Role.CEO, true);
    ceo = memberStore.create(ceo);
    companyStore.refresh(c.pk);
    return c;
  }

  public Company modifyCompany(Company c, String newName, String newLogo, boolean newVerified, boolean newHidden) {
    Company company = new Company(c.pk, newName, "S" + newName,
        c.members, c.accounts, c.services,
        newLogo, newVerified, newHidden);
    companyStore.save(company);
    return company;
  }

  public Member createMember(Company company, Player player) {
    //todo add current date here
    Member newMember = new Member(0, player.getUniqueId(), company.pk, null, Role.Employee, false);
    newMember = memberStore.create(newMember);
    companyStore.refresh(newMember.pk);
    return newMember;
  }

  public Member modifyMember(Member m, Role newRole, boolean newAcceptedInvite) {
    Member member = new Member(m.pk, m.uuid, m.companyPk, m.joinDate, newRole, newAcceptedInvite);
    memberStore.save(member);
    companyStore.refresh(m.companyPk);
    return member;
  }

  public HoldingsAccount createHoldingsAccount(Company company, String name, Player player) {
    HoldingsAccount ha = new HoldingsAccount(0, name, UUID.randomUUID(), company.pk, null);
    ha = holdingsAccountStore.create(ha);

    Holding h = new Holding(0, player.getUniqueId(), ha.pk, 1, 0);
    h = holdingStore.create(h);
    holdingsAccountStore.refresh(ha.pk);
    companyAccountStore.refresh(company.pk);
    return ha;
  }

  public CompanyAccount createCompanyAccount(Company company, String name, Player player) {
    CompanyAccount ca = new CompanyAccount(0, name, UUID.randomUUID(), company.pk, 0);
    ca = companyAccountStore.create(ca);
    companyAccountStore.refresh(company.pk);
    return ca;
  }

  public Account renameAccount(Account account, String newName) {
    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<Account>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, newName, a.uuid, a.companyPk, a.balance);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        HoldingsAccount ha = new HoldingsAccount(a.pk, newName, a.uuid, a.companyPk, a.holdings);
        holdingsAccountStore.save(ha);
        val = ha;
      }
    };
    account.accept(visitor);
    Account a = visitor.getRecentVal();
    companyStore.refresh(a.companyPk);
    return a;
  }

  public Account payAccount(Account account, double amount) {
    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<Account>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, a.name, a.uuid, a.companyPk, a.balance + amount);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        HoldingsAccount ha = new HoldingsAccount(a.pk, a.name, a.uuid, a.companyPk, a.holdings);
        double totalShare = ha.getTotalShare();
        //Add money proportionally to all holdings
        for (Holding h : ha.holdings) {
          Holding newHolding = new Holding(h.pk, h.player, h.accountPk, h.share,  h.balance + (h.share / totalShare) * amount);
          holdingStore.save(newHolding);
        }
        holdingsAccountStore.save(ha);
        //Refresh to account for holding changes
        holdingsAccountStore.refresh(ha.pk);
        val = holdingsAccountStore.get(ha.pk);
      }
    };
    account.accept(visitor);
    Account a = visitor.getRecentVal();
    //TODO create a transaction log
    companyStore.refresh(a.companyPk);
    return a;
  }

  public Account withdrawFromAccount(Account account, double amount) {
    if (amount < 0) {
      System.out.println("Should we be withdrawing a -ve amount?");
      throw new IllegalArgumentException("Tried to withdraw a negative amount");
    }

    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, a.name, a.uuid, a.companyPk, a.balance - amount);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        throw new IllegalArgumentException("Tried to withdraw from a holdings account");
      }
    };
    account.accept(visitor);
    Account a = visitor.getRecentVal();
    //TODO create a transaction log
    companyStore.refresh(a.companyPk);
    return a;
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

}
