package dev.tycho.stonks.db_new;

import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
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
  private Store<Transaction> transactionStore;

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
    transactionStore = new SyncStore<>(null, Transaction::new);
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

  //Find the account with a given id. Will return null if none is found
  public Account accountWithId(int id) {
    Account a = holdingsAccountStore.get(id);
    if (a == null) {
      a = companyAccountStore.get(id);
    }
    return a;
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

  public boolean deleteMember(Member m) {
    if (memberStore.delete(m.pk)) {
      //Update the company for this to remove the member
      companyStore.refresh(m.companyPk);
      return true;
    }
    return false;
  }

  public Collection<Member> getInvites(Player player) {
    return memberStore.getAllWhere(member-> !member.acceptedInvite && member.uuid.equals(player.getUniqueId()));
  }

  public Transaction createTransaction(Player player, Account account, String message, double amount) {
    //Create the new transaction
    Transaction t = new Transaction(0, account.pk, player.getUniqueId(), message, amount,
        new Timestamp(Calendar.getInstance().getTime().getTime()));
    t = transactionStore.create(t);
    //Refresh the respective account object
    account.accept(new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        companyAccountStore.refresh(a.pk);
      }

      @Override
      public void visit(HoldingsAccount a) {
        holdingsAccountStore.refresh(a.pk);
      }
    });
    //Update the company for the account we just updated
    companyStore.refresh(account.companyPk);
    return t;
  }

  public HoldingsAccount createHoldingsAccount(Company company, String name, Player player) {
    HoldingsAccount ha = new HoldingsAccount(0, name, UUID.randomUUID(), company.pk, null, null);
    ha = holdingsAccountStore.create(ha);

    Holding h = new Holding(0, player.getUniqueId(), ha.pk, 1, 0);
    h = holdingStore.create(h);
    holdingsAccountStore.refresh(ha.pk);
    companyStore.refresh(company.pk);
    return ha;
  }

  public CompanyAccount createCompanyAccount(Company company, String name, Player player) {
    CompanyAccount ca = new CompanyAccount(0, name, UUID.randomUUID(), company.pk, null, 0);
    ca = companyAccountStore.create(ca);
    companyStore.refresh(company.pk);
    return ca;
  }

  public Account renameAccount(Account account, String newName) {
    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<Account>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, newName, a.uuid, a.companyPk, a.transactions, a.balance);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        HoldingsAccount ha = new HoldingsAccount(a.pk, newName, a.uuid, a.companyPk, a.transactions, a.holdings);
        holdingsAccountStore.save(ha);
        val = ha;
      }
    };
    account.accept(visitor);
    Account a = visitor.getRecentVal();
    companyStore.refresh(a.companyPk);
    return a;
  }

  public Account payAccount(Player player, String message, Account account, double amount) {
    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<Account>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, a.name, a.uuid, a.companyPk, a.transactions, a.balance + amount);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        HoldingsAccount ha = new HoldingsAccount(a.pk, a.name, a.uuid, a.companyPk, a.transactions, a.holdings);
        double totalShare = ha.getTotalShare();
        //Add money proportionally to all holdings
        for (Holding h : ha.holdings) {
          Holding newHolding = new Holding(h.pk, h.player, h.accountPk, h.share, h.balance + (h.share / totalShare) * amount);
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
    //Create a transaction log too
    createTransaction(player, account, message, amount);
    //We don't need to refresh the company because this is done when creating a transaction log
    //companyStore.refresh(a.companyPk);
    return a;
  }

  public Account withdrawFromAccount(Player player, Account account, double amount) {
    if (amount < 0) {
      System.out.println("Should we be withdrawing a -ve amount?");
      throw new IllegalArgumentException("Tried to withdraw a negative amount");
    }

    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, a.name, a.uuid, a.companyPk, a.transactions, a.balance - amount);
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
    //Create a transaction log too
    createTransaction(player, account, "withdraw", amount);
    //We don't need to refresh the company because this is done when creating a transaction log
    //companyStore.refresh(a.companyPk);
    return a;
  }

  public Holding createHolding(UUID player, HoldingsAccount holdingsAccount, double share) {
    Holding holding = new Holding(0, player, holdingsAccount.pk, share, 0);
    holding = holdingStore.create(holding);
    if (holding == null) {
      // error creating holding, we should never get here
      return null;
    }
    //Update account and company to persist the new holding
    holdingsAccountStore.refresh(holding.accountPk);
    companyStore.refresh(holdingsAccount.companyPk);
    return holding;
  }

  public boolean deleteHolding(Holding holding) {
    if (memberStore.delete(holding.pk)) {
      //Update the company and account
      holdingsAccountStore.refresh(holding.accountPk);
      HoldingsAccount ha = holdingsAccountStore.get(holding.accountPk);
      companyStore.refresh(ha.companyPk);
      return true;
    }
    return false;
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
