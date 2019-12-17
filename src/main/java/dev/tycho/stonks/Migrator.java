package dev.tycho.stonks;

import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.stonks.managers.DatabaseManager;
import dev.tycho.stonks.model.accountvisitors.ReturningAccountVisitor;
import dev.tycho.stonks.model.core.*;
import dev.tycho.stonks.model.logging.Transaction;
import dev.tycho.stonks.model.service.Service;
import dev.tycho.stonks.model.service.Subscription;
import dev.tycho.stonks2.Repo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Migrator {

  Repo repo;
  DatabaseManager databaseManager;

  public Migrator(Stonks stonks, DatabaseManager databaseManager) {
    repo = new Repo(stonks);
    repo.enable();
    this.databaseManager = databaseManager;

  }

  public void migrate() {
    List<Company> list;
    try {
      QueryBuilder<Company, UUID> companyQueryBuilder = databaseManager.getCompanyDao().queryBuilder();
      companyQueryBuilder.orderBy("name", true);
      list = companyQueryBuilder.query();
    } catch (SQLException e) {
      e.printStackTrace();
      return;
    }

    HashMap<Company, dev.tycho.stonks2.model.core.Company> companyMap = new HashMap<>();
    HashMap<AccountLink, dev.tycho.stonks2.model.core.Account> accountMap = new HashMap<>();

    for (Company company : list) {
      Member ceo = company.getMembers().stream().filter(m -> m.getRole() == Role.CEO).findAny().get();
      dev.tycho.stonks2.model.core.Company newCompany = Repo.getInstance().createCompany(company.getName(), ceo.getUuid());
      companyMap.put(company, newCompany);
      System.out.println("Migrated company, pk = " + newCompany.pk + ", name = " + newCompany.name);
      //We have a company with a ceo
      //We now need to add the rest of the members
      for (Member member : company.getMembers()) {
        if (member.getRole() == Role.CEO) continue;
        dev.tycho.stonks2.model.core.Member newMember = Repo.getInstance().createMember(newCompany, member.getUuid());
        System.out.println("\tMigrated member, pk = " + newMember.pk);
      }
      System.out.println();
      System.out.println();
      //Now do accounts
      for (AccountLink accountLink : company.getAccounts()) {
        Account account = accountLink.getAccount();
        ReturningAccountVisitor<dev.tycho.stonks2.model.core.Account> visitor
            = new ReturningAccountVisitor<dev.tycho.stonks2.model.core.Account>() {
          @Override
          public void visit(CompanyAccount a) {
            dev.tycho.stonks2.model.core.CompanyAccount ca = new dev.tycho.stonks2.model.core.CompanyAccount(
                0,
                a.getName(),
                a.getUuid(),
                newCompany.pk,
                new ArrayList<>(),
                a.getTotalBalance());
            ca = Repo.getInstance().companyAccounts().create(ca);
            val = ca;
            System.out.println("\tMigrated company account, pk = " + ca.pk + ", name = " + ca.name);
          }

          @Override
          public void visit(HoldingsAccount a) {
            dev.tycho.stonks2.model.core.HoldingsAccount ha = new dev.tycho.stonks2.model.core.HoldingsAccount(
                0,
                a.getName(),
                a.getUuid(),
                newCompany.pk,
                new ArrayList<>(),
                new ArrayList<>());
            ha = Repo.getInstance().holdingsAccounts().create(ha);
            val = ha;
            System.out.println("\tMigrated holdings account, pk = " + ha.pk + ", name = " + ha.name);

            //Now load holdings
            for (Holding holding : a.getHoldings()) {
              dev.tycho.stonks2.model.core.Holding h = new dev.tycho.stonks2.model.core.Holding(
                  0,
                  holding.getPlayer(),
                  holding.getBalance(),
                  holding.getShare(),
                  ha.pk
              );
              h = Repo.getInstance().holdings().create(h);
              System.out.println("\t\tMigrated holding, pk = " + h.pk + ", player = " + h.playerUUID);
              Repo.getInstance().holdingsAccounts().refreshRelations(ha.pk);
            }
          }
        };
        account.accept(visitor);
        dev.tycho.stonks2.model.core.Account newAccount = visitor.getRecentVal();
        accountMap.put(accountLink, newAccount);
        Repo.getInstance().companies().refreshRelations(newCompany.pk);
        //Now do transactions
        List<Transaction> transactions = databaseManager.getTransactionDao()
            .getTransactionsForAccount(accountLink, databaseManager.getAccountLinkDao().queryBuilder(), 0, 1000000);
        for (Transaction transaction : transactions) {
          dev.tycho.stonks2.model.logging.Transaction t = new dev.tycho.stonks2.model.logging.Transaction(
              0,
              newAccount.pk,
              transaction.getPayee(),
              transaction.getMessage(),
              transaction.getAmount(),
              transaction.getTimestamp()
          );
          Repo.getInstance().transactions().create(t);
          System.out.println("\t\tMigrated transaction, message = " + t.message);

        }

        System.out.println();
        System.out.println();


      }
      //Now do services
      for (Service service : company.getServices()) {
        dev.tycho.stonks2.model.service.Service newService = new dev.tycho.stonks2.model.service.Service(
            0,
            service.getName(),
            service.getDuration(),
            service.getCost(),
            service.getMaxSubscriptions(),
            accountMap.get(service.getAccount()).pk,
            new ArrayList<>());
        newService = Repo.getInstance().services().create(newService);
        System.out.println("\tMigrated service, pk = " + newService.pk + " name = " + newService.name);

        for (Subscription subscription : service.getSubscriptions()) {
          dev.tycho.stonks2.model.service.Subscription newSubscription = new dev.tycho.stonks2.model.service.Subscription(
              0,
              subscription.getPlayerId(),
              newService.pk,
              subscription.getLastPaymentDate(),
              true);
          newSubscription = Repo.getInstance().subscriptions().create(newSubscription);
          System.out.println("\t\tMigrated subscription, pk = " + newSubscription.pk + " player = " + newSubscription.playerUUID);
        }
      }
    }
  }
}
