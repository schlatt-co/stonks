package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.db_new.Store;
import dev.tycho.stonks.db_new.SyncStore;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.dbis.*;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

//The repo has a store for each entity we want to save in the database
public class Repo extends SpigotModule {

  private static Repo instance;
  public static Repo getInstance() {
    return instance;
  }

  private Connection conn;
  private SyncStore<Company> companyStore;
  private SyncStore<CompanyAccount> companyAccountStore;
  private SyncStore<HoldingsAccount> holdingsAccountStore;
  private SyncStore<Holding> holdingStore;
  private SyncStore<Member> memberStore;
  private SyncStore<Service> serviceStore;
  private SyncStore<Subscription> subscriptionStore;
  private SyncStore<Transaction> transactionStore;

  private final Stonks plugin;

  public Repo(Stonks stonks) {
    super("Repository", stonks);
    instance = this;
    this.plugin = stonks;
  }

  private Connection createConnection() throws SQLException {
    String host = plugin.getConfig().getString("mysql.host");
    String port = plugin.getConfig().getString("mysql.port");
    String database = plugin.getConfig().getString("mysql.database");
    String username = plugin.getConfig().getString("mysql.username");
    String password = plugin.getConfig().getString("mysql.password");
    String useSsl = plugin.getConfig().getString("mysql.ssl");
    String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
        "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL="
        + useSsl;

    Properties connectionProps = new Properties();
    connectionProps.put("user", username);
    connectionProps.put("password", password);
    Connection conn = DriverManager.getConnection(url, connectionProps);
    System.out.println("Connected to database");
    return conn;
  }

  @Override
  public void enable() {
    try {
      conn = createConnection();
    } catch (SQLException e) {
      e.printStackTrace();
      return;
    }

    companyStore = new SyncStore<>(Company::new);
    companyAccountStore = new SyncStore<>(CompanyAccount::new);
    holdingsAccountStore = new SyncStore<>(HoldingsAccount::new);
    holdingStore = new SyncStore<>(Holding::new);
    memberStore = new SyncStore<>(Member::new);
    serviceStore = new SyncStore<>(Service::new);
    subscriptionStore = new SyncStore<>(Subscription::new);
    transactionStore = new SyncStore<>(Transaction::new);

    companyStore.setDbi(new CompanyDBI(conn, memberStore, companyAccountStore, holdingsAccountStore));
    companyAccountStore.setDbi(new CompanyAccountDBI(conn, transactionStore, serviceStore));
    holdingsAccountStore.setDbi(new HoldingsAccountDBI(conn, transactionStore, serviceStore, holdingStore));
    holdingStore.setDbi(new HoldingDBI(conn));
    memberStore.setDbi(new MemberDBI(conn));
    serviceStore.setDbi(new ServiceDBI(conn, subscriptionStore));
    subscriptionStore.setDbi(new SubscriptionDBI(conn));
    transactionStore.setDbi(new TransactionDBI(conn));

    subscriptionStore.populate();
    serviceStore.populate();
    transactionStore.populate();
    companyAccountStore.populate();
    holdingStore.populate();
    holdingsAccountStore.populate();
    memberStore.populate();
    companyStore.populate();
  }



  public Collection<Company> companiesWhereManager(Player player) {
    Collection<Company> list = new ArrayList<>(Repo.getInstance().companies().getAll());
    list.removeIf(c -> {
      for (Member m : c.members) {
        //If a manager then keep
        if (m.playerUUID.equals(player.getUniqueId()) && m.hasManagamentPermission()) return false;
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

  public Account accountWithUUID(UUID uuid) {
    Account a = holdingsAccountStore.getWhere(c -> c.uuid.equals(uuid));
    if (a == null) {
      a = companyAccountStore.getWhere(c -> c.uuid.equals(uuid));
    }
    return a;
  }


  public Company createCompany(String companyName, Player player) {
    Company c = new Company(0, companyName, "S" + companyName,
        Material.EMERALD.name(), false, false, new ArrayList<>(), new ArrayList<>()
    );
    c = companyStore.create(c);

    java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
    Member ceo = new Member(0, player.getUniqueId(), c.pk, currentDate, Role.CEO, true);
    ceo = memberStore.create(ceo);
    companyStore.refresh(c.pk);
    return c;
  }

  public Company modifyCompany(Company c, String newName, String newLogo, boolean newVerified, boolean newHidden) {
    Company company = new Company(c.pk, newName, "S" + newName,
        newLogo, newHidden, newVerified, c.accounts, c.members
    );
    companyStore.save(company);
    return company;
  }

  public Member createMember(Company company, Player player) {
    java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
    Member newMember = new Member(0, player.getUniqueId(), company.pk, currentDate, Role.Employee, false);
    newMember = memberStore.create(newMember);
    companyStore.refresh(newMember.pk);
    return newMember;
  }

  public Member modifyMember(Member m, Role newRole, boolean newAcceptedInvite) {
    Member member = new Member(m.pk, m.playerUUID, m.companyPk, m.joinDate, newRole, newAcceptedInvite);
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
    return memberStore.getAllWhere(member -> !member.acceptedInvite && member.playerUUID.equals(player.getUniqueId()));
  }

  public Transaction createTransaction(UUID player, Account account, String message, double amount) {
    //Create the new transaction
    Transaction t = new Transaction(0, account.pk, player, message, amount,
        new java.sql.Date(Calendar.getInstance().getTime().getTime()));
    t = transactionStore.create(t);
    //Refresh the respective account object
    refreshAccount(account);
    //Update the company for the account we just updated
    companyStore.refresh(account.companyPk);
    return t;
  }

  public HoldingsAccount createHoldingsAccount(Company company, String name, Player player) {
    HoldingsAccount ha = new HoldingsAccount(0, name, UUID.randomUUID(), company.pk, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    ha = holdingsAccountStore.create(ha);

    Holding h = new Holding(0, player.getUniqueId(), 0, 1, ha.pk);
    h = holdingStore.create(h);
    holdingsAccountStore.refresh(ha.pk);
    companyStore.refresh(company.pk);
    return ha;
  }

  public CompanyAccount createCompanyAccount(Company company, String name, Player player) {
    CompanyAccount ca = new CompanyAccount(0, name, UUID.randomUUID(), company.pk, new ArrayList<>(), new ArrayList<>(), 0);
    ca = companyAccountStore.create(ca);
    companyStore.refresh(company.pk);
    return ca;
  }

  private void refreshAccount(Account account) {
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
  }

  public Account renameAccount(Account account, String newName) {
    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<Account>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, newName, a.uuid, a.companyPk, a.transactions, a.services, a.balance);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        HoldingsAccount ha = new HoldingsAccount(a.pk, newName, a.uuid, a.companyPk, a.transactions, a.services, a.holdings);
        holdingsAccountStore.save(ha);
        val = ha;
      }
    };
    account.accept(visitor);
    Account a = visitor.getRecentVal();
    companyStore.refresh(a.companyPk);
    return a;
  }

  public Account payAccount(UUID player, String message, Account account, double amount) {
    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<Account>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, a.name, a.uuid, a.companyPk, a.transactions, a.services, a.balance + amount);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        HoldingsAccount ha = new HoldingsAccount(a.pk, a.name, a.uuid, a.companyPk, a.transactions, a.services, a.holdings);
        double totalShare = ha.getTotalShare();
        //Add money proportionally to all holdings
        for (Holding h : ha.holdings) {
          Holding newHolding = new Holding(h.pk, h.playerUUID, h.balance + (h.share / totalShare) * amount, h.share, h.accountPk);
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

  public Account withdrawFromAccount(UUID player, Account account, double amount) {
    if (amount < 0) {
      System.out.println("Should we be withdrawing a -ve amount?");
      throw new IllegalArgumentException("Tried to withdraw a negative amount");
    }

    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, a.name, a.uuid, a.companyPk, a.transactions, a.services, a.balance - amount);
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
    Holding holding = new Holding(0, player, 0, share, holdingsAccount.pk);
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

  public Service createService(String name, double duration, double cost, int maxSubscribers, Account account) {
    Service service = new Service(0, name, duration, cost, maxSubscribers, account.pk, new ArrayList<>());
    service = serviceStore.create(service);
    refreshAccount(account);
    companyStore.refresh(account.companyPk);
    return service;
  }

  public Service modifyService(Service s, String name, double duration, double cost, int maxSubscribers) {
    Service service = new Service(s.pk, s.name, duration, cost, maxSubscribers, s.accountPk, s.subscriptions);
    serviceStore.save(service);
    Account a = accountWithId(service.accountPk);
    refreshAccount(a);
    companyStore.refresh(a.companyPk);
    return service;
  }

  public Subscription createSubscription(Player player, Service service, boolean autoPay) {
    Subscription subscription = new Subscription(0, player.getUniqueId(), service.pk, new java.sql.Date(Calendar.getInstance().getTime().getTime()), autoPay);
    subscription = subscriptionStore.create(subscription);
    serviceStore.refresh(service.pk);
    Account account = accountWithId(service.accountPk);
    refreshAccount(account);
    companyStore.refresh(account.companyPk);
    return subscription;
  }

  public Subscription paySubscription(UUID player, Subscription subscription, Service service) {
    Subscription s = new Subscription(subscription.pk, subscription.playerUUID, subscription.servicePk,
        new java.sql.Date(Calendar.getInstance().getTime().getTime()), subscription.autoPay);

    subscriptionStore.save(s);
    if (service.pk != s.servicePk) throw new IllegalArgumentException("Primary Key mismatch");
    serviceStore.refresh(subscription.servicePk);
    //This action also refreshes the account and company
    payAccount(player, "Subscription payment (" + service.name + ")", accountWithId(service.accountPk), service.cost);
    return s;
  }

  public boolean deleteSubscription(Subscription subscription, Service service) {
    if (subscriptionStore.delete(subscription.pk)) {
      serviceStore.refresh(service.pk);
      Account account = accountWithId(service.accountPk);
      refreshAccount(account);
      companyStore.refresh(account.companyPk);
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
