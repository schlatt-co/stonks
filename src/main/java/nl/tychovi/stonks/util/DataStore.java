package nl.tychovi.stonks.util;

import nl.tychovi.stonks.model.Account;
import nl.tychovi.stonks.model.Company;
import nl.tychovi.stonks.model.CompanyAccount;
import nl.tychovi.stonks.model.Entity;

import java.util.ArrayList;
import java.util.List;

public class DataStore {

  private List<Entity> entities = new ArrayList<>();

  private List<Company> companies = new ArrayList<>();
  private DatabaseConnector connector;

  public DataStore(DatabaseConnector connector) {
    this.connector = connector;
    loadModel();
  }

  public void loadModel() {
    //Load all companies
    //Then load the accounts for each company
    companies.addAll(connector.getCompanies());
    for (Company c : companies) {
      for (Account a : connector.getAccountsForCompany(c)) {
        c.addAccount(a);
      }
    }
  }


  public void saveModel() {
    for (Company c : companies) {
      for (Account a : c.getAccounts()) {
        updateAccount(a);
      }
      updateCompany(c);
    }
  }

  public boolean createCompanyAccount(Company c, String name, String creator_uuid) {
    int id = connector.createCompanyAccount(c, name, creator_uuid);
    if (id == -1) return false;
    CompanyAccount newAccount = new CompanyAccount(id, name, 0);
    c.addAccount(newAccount);
    return true;
  }

  public boolean createCompany(String name, String creator_uuid) {
    int id = connector.createCompany(name, creator_uuid);
    if (id == -1) return false;
    Company newCompany = new Company(id, name);
    companies.add(newCompany);
    return true;
  }


  public void updateCompany(Company c) {
    connector.saveCompany(c);
  }

  public void updateAccount(Account a) {
    connector.saveAccount(a);
  }

  //Returns null if no company is found
  public Company getCompanyByName(String name) {
    for (Company c : companies) {
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  public List<Company> getCompanies() {
    return companies;
  }


}
