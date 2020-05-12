package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.api.event.TransactionLogEvent;
import dev.tycho.stonks.database.DatabaseStore;
import dev.tycho.stonks.database.Store;
import dev.tycho.stonks.database.SyncStore;
import dev.tycho.stonks.database.TransactionStore;
import dev.tycho.stonks.model.accountvisitors.IAccountVisitor;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.dbis.*;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import dev.tycho.stonks.util.StonksUser;
import dev.tycho.stonks.util.Util;
import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

//The repo has a store for each entity we want to save in the database
public class Repo extends SpigotModule {

  private static Repo instance;
  private final Stonks plugin;
  private BasicDataSource dataSource;

  private DatabaseStore<Company> companyStore;
  private DatabaseStore<CompanyAccount> companyAccountStore;
  private DatabaseStore<HoldingsAccount> holdingsAccountStore;
  private DatabaseStore<Holding> holdingStore;
  private DatabaseStore<Member> memberStore;
  private DatabaseStore<Service> serviceStore;
  private DatabaseStore<Subscription> subscriptionStore;
  private DatabaseStore<Perk> perkStore;
  private TransactionStore transactionStore;

  public Repo(Stonks stonks) {
    super("Database Manager", stonks);
    instance = this;
    dataSource = new BasicDataSource();
    this.plugin = stonks;
  }

  public static Repo getInstance() {
    if (!Util.isCalledInternally()) {
      throw new RuntimeException("Improper use of internal stonks classes.");
    }
    return instance;
  }

  private void createDataSource() {
    String host = plugin.getConfig().getString("mysql.host");
    String port = plugin.getConfig().getString("mysql.port");
    String database = plugin.getConfig().getString("mysql.database");
    String username = plugin.getConfig().getString("mysql.username");
    String password = plugin.getConfig().getString("mysql.password");
    String useSsl = plugin.getConfig().getString("mysql.ssl");
    String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
        "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL="
        + useSsl;
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    dataSource.setMinIdle(5);
    dataSource.setMaxIdle(10);
    dataSource.setMaxOpenPreparedStatements(100);
    System.out.println("Connected to database");
  }

  //todo this could be in a better place than repo
  public int getNextAccountPk(Connection conn) {
    try {
      int companyAccountPk = -1;
      int holdingsAccountPk = -1;
      PreparedStatement statement = conn.prepareStatement(
          "SHOW TABLE STATUS LIKE 'company_account'");
      ResultSet results = statement.executeQuery();
      while (results.next()) {
        companyAccountPk = results.getInt("Auto_increment");
      }
      statement = conn.prepareStatement(
          "SHOW TABLE STATUS LIKE 'holdings_account'");
      results = statement.executeQuery();
      while (results.next()) {
        holdingsAccountPk = results.getInt("Auto_increment");
      }
      if (holdingsAccountPk == -1 || companyAccountPk == -1) {
        System.out.println("Couldn't get a primary key for an account");
        return -1;
      }

      return Math.max(companyAccountPk, holdingsAccountPk);
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  @Override
  public void enable() {
    createDataSource();

    companyStore = new SyncStore<>();
    companyAccountStore = new SyncStore<>();
    holdingsAccountStore = new SyncStore<>();
    holdingStore = new SyncStore<>();
    memberStore = new SyncStore<>();
    serviceStore = new SyncStore<>();
    subscriptionStore = new SyncStore<>();
    perkStore = new SyncStore<>();
    transactionStore = new TransactionStore(dataSource, new TransactionDBI(dataSource));
    transactionStore.createTable();


    companyStore.setDbi(new CompanyDBI(dataSource, memberStore, companyAccountStore, holdingsAccountStore, perkStore));
    perkStore.setDbi(new PerkDBI(dataSource));
    companyAccountStore.setDbi(new CompanyAccountDBI(dataSource, serviceStore));
    holdingsAccountStore.setDbi(new HoldingsAccountDBI(dataSource, serviceStore, holdingStore));
    holdingStore.setDbi(new HoldingDBI(dataSource));
    memberStore.setDbi(new MemberDBI(dataSource));
    serviceStore.setDbi(new ServiceDBI(dataSource, subscriptionStore));
    subscriptionStore.setDbi(new SubscriptionDBI(dataSource));

    repopulateAll();
  }


  public void repopulateAll() {
    subscriptionStore.populate();
    serviceStore.populate();
    companyAccountStore.populate();
    perkStore.populate();
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

  public Collection<Company> companiesWhereMember(Player player) {
    Collection<Company> list = new ArrayList<>(Repo.getInstance().companies().getAll());
    list.removeIf(c -> {
      for (Member m : c.members) {
        //If a member then keep
        if (m.playerUUID.equals(player.getUniqueId()) && m.acceptedInvite) {
          return false;
        }
      }
      return true;
    });
    return list;
  }

  public Collection<Company> companiesWithWithdrawableAccount(Player player) {
    List<Company> list = new ArrayList<>(Repo.getInstance().companies().getAll());
    //We need a list of all companies with a withdrawable account for this player
    //Remove companies where the player is not a manager and doesn't have an account
    //todo remove this messy logic
    for (int i = list.size() - 1; i >= 0; i--) {
      boolean remove = true;
      Company c = list.get(i);
      Member m = c.getMember(player);
      if (m != null && m.hasManagamentPermission()) {
        //If a manager or ceo
        remove = false;
      }
      //If you are not a manager, or a non-member with a holding then don't remove
      for (Account a : c.accounts) {
        //Is there a holding account for the player
        ReturningAccountVisitor<Boolean> visitor = new ReturningAccountVisitor<>() {
          @Override
          public void visit(CompanyAccount a) {
            val = false;
          }

          @Override
          public void visit(HoldingsAccount a) {
            val = (a.getPlayerHolding(player.getUniqueId()) != null);
          }
        };
        a.accept(visitor);
        if (visitor.getRecentVal()) remove = false;
      }
      if (remove) list.remove(i);
    }
    return list;
  }


  public void sendMessageToPlayer(UUID playerUUID, String message) {
    StonksUser u = Stonks.getUser(playerUUID);
    Player player = u.getBase();
    if (player == null || !player.isOnline()) return;
    sendMessage(player, message);
  }

  public void sendMessageToAllOnlineManagers(Company company, String message) {
    for (Member member : company.members) {
      if (member.hasManagamentPermission()) {
        sendMessageToPlayer(member.playerUUID, message);
      }
    }
  }

  public void sendMessageToAllOnlineMembers(Company company, String message) {
    for (Member member : company.members) {
      sendMessageToPlayer(member.playerUUID, message);
    }
  }

  public Company companyWithName(String name) {
    return Repo.getInstance().companies().getWhere(c -> c.name.equals(name));
  }

  //Find the account with a given pk. Will return null if none is found
  public Account accountWithPk(int pk) {
    Account a = holdingsAccountStore.get(pk);
    if (a == null) {
      a = companyAccountStore.get(pk);
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
        Material.EMERALD.name(), false, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
    );
    c = companyStore.create(c);

    if (player != null) {
      Member ceo = new Member(0, player.getUniqueId(), c.pk, new Timestamp(System.currentTimeMillis()), Role.CEO, true);
      memberStore.create(ceo);
    }
    //Create a default company account
    createCompanyAccount(c, "Main");
    companyStore.refreshRelations(c.pk);
    return c;
  }

  public Company modifyCompany(int companyPk, String newName, String newLogo, boolean newVerified, boolean newHidden) {
    Company old = companies().get(companyPk);
    Company company = new Company(old.pk, newName, "S" + newName,
        newLogo, newVerified, newHidden, old.accounts, old.members, old.perks
    );
    companyStore.save(company);
    return company;
  }

  public Perk createPerk(Company company, String namespace) {
    Perk p = new Perk(0, company.pk, namespace);
    p = perkStore.create(p);
    //Update the company store for the company in which we created a perk
    companyStore.refreshRelations(company.pk);
    return p;
  }

  public boolean deletePerk(Perk perk) {
    if (perkStore.delete(perk.pk)) {
      //Update the company for this to remove the member
      companyStore.refreshRelations(perk.companyPk);
      return true;
    }
    return false;
  }


  public Member createMember(Company company, Player player) {
    Member newMember = new Member(0, player.getUniqueId(), company.pk, new Timestamp(System.currentTimeMillis()), Role.Employee, false);
    newMember = memberStore.create(newMember);
    companyStore.refreshRelations(newMember.companyPk);
    return newMember;
  }

  public Member modifyMember(int memberPk, Role newRole, boolean newAcceptedInvite) {
    Member old = members().get(memberPk);
    Member member = new Member(old.pk, old.playerUUID, old.companyPk, old.joinTimestamp, newRole, newAcceptedInvite);
    memberStore.save(member);
    companyStore.refreshRelations(old.companyPk);
    return member;
  }

  public boolean deleteMember(Member m) {
    if (memberStore.delete(m.pk)) {
      //Update the company for this to remove the member
      companyStore.refreshRelations(m.companyPk);
      return true;
    }
    return false;
  }

  public Collection<Member> getInvites(Player player) {
    return memberStore.getAllWhere(member -> !member.acceptedInvite && member.playerUUID.equals(player.getUniqueId()));
  }

  public void createTransaction(UUID player, Account account, String message, double amount) {
    //Create the new transaction
    Transaction t = new Transaction(0, account.pk, player, message, amount,
        new Timestamp(System.currentTimeMillis()));
    transactionStore.create(t);
    Company company = companies().get(account.companyPk);
    // TODO find a solution for this
    // Don't create log events for null players, e.g. on a shop event
    if (player != null)
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> Bukkit.getPluginManager().callEvent(new TransactionLogEvent(company, account, t, Stonks.getUser(player).getBase())));
    //No refreshes are needed since no entities have a collection of transactions
  }

  public HoldingsAccount createHoldingsAccount(Company company, String name, Player player) {
    HoldingsAccount ha = new HoldingsAccount(0, name, UUID.randomUUID(), company.pk, new ArrayList<>(), new ArrayList<>());
    ha = holdingsAccountStore.create(ha);

    Holding h = new Holding(0, player.getUniqueId(), 0, 1, ha.pk);
    holdingStore.create(h);
    holdingsAccountStore.refreshRelations(ha.pk);
    companyStore.refreshRelations(company.pk);
    return ha;
  }

  public CompanyAccount createCompanyAccount(Company company, String name) {
    CompanyAccount ca = new CompanyAccount(0, name, UUID.randomUUID(), company.pk, new ArrayList<>(), 0);
    ca = companyAccountStore.create(ca);
    companyStore.refreshRelations(company.pk);
    return ca;
  }

  private void refreshAccount(Account account) {
    account.accept(new IAccountVisitor() {
      @Override
      public void visit(CompanyAccount a) {
        companyAccountStore.refreshRelations(a.pk);
      }

      @Override
      public void visit(HoldingsAccount a) {
        holdingsAccountStore.refreshRelations(a.pk);
      }
    });
  }

  public Account renameAccount(Account account, String newName) {
    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, newName, a.uuid, a.companyPk, a.services, a.balance);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        HoldingsAccount ha = new HoldingsAccount(a.pk, newName, a.uuid, a.companyPk, a.services, a.holdings);
        holdingsAccountStore.save(ha);
        val = ha;
      }
    };
    account.accept(visitor);
    Account a = visitor.getRecentVal();
    companyStore.refreshRelations(a.companyPk);
    return a;
  }

  public Account payAccount(UUID player, String message, int accountPk, double amount) {
    Account account = accountWithPk(accountPk);
    ReturningAccountVisitor<Account> visitor = new ReturningAccountVisitor<>() {
      @Override
      public void visit(CompanyAccount a) {
        CompanyAccount ca = new CompanyAccount(a.pk, a.name, a.uuid, a.companyPk, a.services, a.balance + amount);
        companyAccountStore.save(ca);
        val = ca;
      }

      @Override
      public void visit(HoldingsAccount a) {
        HoldingsAccount ha = new HoldingsAccount(a.pk, a.name, a.uuid, a.companyPk, a.services, a.holdings);
        double totalShare = ha.getTotalShare();
        //Add money proportionally to all holdings
        for (Holding h : ha.holdings) {
          Holding newHolding = new Holding(h.pk, h.playerUUID, h.balance + (h.share / totalShare) * amount, h.share, h.accountPk);
          holdingStore.save(newHolding);
        }
        holdingsAccountStore.save(ha);
        //Refresh to account for holding changes
        holdingsAccountStore.refreshRelations(ha.pk);
        val = holdingsAccountStore.get(ha.pk);
      }
    };
    account.accept(visitor);
    Account a = visitor.getRecentVal();
    //Create a transaction log too
    createTransaction(player, account, message, amount);
    //Refresh the accounts for our parent company
    companyStore.refreshRelations(a.companyPk);
    return a;
  }

  public Account withdrawFromAccount(UUID player, CompanyAccount a, double amount) {
    return withdrawFromAccount(player, a, amount, "Withdraw");
  }

  public Account withdrawFromAccount(UUID player, CompanyAccount a, double amount, String message) {
    a = companyAccounts().get(a.pk);
    if (amount < 0) {
      System.out.println("Should we be withdrawing a -ve amount?");
      throw new IllegalArgumentException("Tried to withdraw a negative amount");
    }
    CompanyAccount ca = new CompanyAccount(a.pk, a.name, a.uuid, a.companyPk, a.services, a.balance - amount);
    companyAccountStore.save(ca);
    //Create a transaction log too
    createTransaction(player, a, message, -amount);
    //Refresh the accounts for our parent company
    companyStore.refreshRelations(a.companyPk);
    return ca;
  }

  public Holding createHolding(UUID player, HoldingsAccount holdingsAccount, double share) {
    Holding holding = new Holding(0, player, 0, share, holdingsAccount.pk);
    holding = holdingStore.create(holding);
    if (holding == null) {
      // error creating holding, we should never get here
      return null;
    }
    //Update account and company to persist the new holding
    holdingsAccountStore.refreshRelations(holding.accountPk);
    companyStore.refreshRelations(holdingsAccount.companyPk);
    return holding;
  }

  public Holding withdrawFromHolding(UUID player, int holdingPk, double amount) {
    return withdrawFromHolding(player, holdingPk, amount, "Withdrew from holding");
  }

  public Holding withdrawFromHolding(UUID player, int holdingPk, double amount, String message) {
    Holding h = holdings().get(holdingPk);
    Holding holding = new Holding(h.pk, h.playerUUID, h.balance - amount, h.share, h.accountPk);
    holdingStore.save(holding);
    holdingsAccountStore.refreshRelations(h.accountPk);

    createTransaction(player, accountWithPk(h.accountPk), message, -amount);
    Account account = accountWithPk(h.accountPk);
    //Refresh the account and company's relation collections for the new holding
    refreshAccount(account);
    companyStore.refreshRelations(account.companyPk);

    return holding;
  }

  public boolean removeHolding(Holding holding, UUID player) {
    double balance = holding.balance;
    if (holdingStore.delete(holding.pk)) {
      //Update the company and account
      holdingsAccountStore.refreshRelations(holding.accountPk);
      HoldingsAccount ha = holdingsAccountStore.get(holding.accountPk);
      companyStore.refreshRelations(ha.companyPk);
      payAccount(player, "Holding removal payout", ha.pk, balance);
      return true;
    }
    return false;
  }

  public Service createService(String name, double duration, double cost, int maxSubscribers, Account account) {
    Service service = new Service(0, name, duration, cost, maxSubscribers, account.pk, new ArrayList<>());
    service = serviceStore.create(service);
    refreshAccount(account);
    companyStore.refreshRelations(account.companyPk);
    return service;
  }

  public Service modifyService(int servicePk, String name, double duration, double cost, int maxSubscribers) {
    Service s = services().get(servicePk);
    Service service = new Service(s.pk, s.name, duration, cost, maxSubscribers, s.accountPk, s.subscriptions);
    serviceStore.save(service);
    Account a = accountWithPk(service.accountPk);
    refreshAccount(a);
    companyStore.refreshRelations(a.companyPk);
    return service;
  }

  public Subscription createSubscription(Player player, Service service, boolean autoPay) {
    Subscription subscription = new Subscription(0, player.getUniqueId(), service.pk, new Timestamp(System.currentTimeMillis()), autoPay);
    subscription = subscriptionStore.create(subscription);
    serviceStore.refreshRelations(service.pk);
    Account account = accountWithPk(service.accountPk);
    refreshAccount(account);
    companyStore.refreshRelations(account.companyPk);
    return subscription;
  }

  public Subscription paySubscription(UUID player, Subscription subscription, Service service) {
    if (subscription.servicePk != service.pk)
      throw new IllegalArgumentException("Subscription does not belong to service");
    Subscription s = new Subscription(subscription.pk, subscription.playerUUID, subscription.servicePk,
        new Timestamp(System.currentTimeMillis()), subscription.autoPay);

    subscriptionStore.save(s);
    if (service.pk != s.servicePk) throw new IllegalArgumentException("Primary Key mismatch");
    serviceStore.refreshRelations(subscription.servicePk);
    //This action also refreshes the account and company
    payAccount(player, "Subscription payment (" + service.name + ")", service.accountPk, service.cost);
    return s;
  }

  public boolean deleteSubscription(Subscription subscription, Service service) {
    if (subscriptionStore.delete(subscription.pk)) {
      serviceStore.refreshRelations(service.pk);
      Account account = accountWithPk(service.accountPk);
      refreshAccount(account);
      companyStore.refreshRelations(account.companyPk);
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

  public TransactionStore transactions() {
    return transactionStore;
  }

}
