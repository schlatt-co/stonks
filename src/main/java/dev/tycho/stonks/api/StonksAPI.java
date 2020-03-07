package dev.tycho.stonks.api;

import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.managers.PerkManager;
import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.core.Account;
import dev.tycho.stonks.model.core.Company;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StonksAPI {

  /**
   * Registers a perk for companies.
   *
   * @param perk Perk to register
   */
  public static void registerPerk(CompanyPerk perk) {
    PerkManager.getInstance().registerPerk(perk);
  }

  /**
   * Returns the Main account for the Admins company.
   *
   * @return The main admin company account.
   * @throws StonksAPIException Should be never thrown.
   */
  public static Account getAdminAccount() throws StonksAPIException {
    return getOrCreateAccount(getAdminCompany(), "Main");
  }

  /**
   * Creates (if one isn't already present) and returns the Admins company.
   *
   * @return The admin company object.
   * @throws StonksAPIException Should be never thrown.
   */
  public static Company getAdminCompany() throws StonksAPIException {
    Company company = getCompany("Admins");
    if (company == null) {
      company = createCompany("Admins", null);
    }
    return company;
  }

  /**
   * Returns the company of the given name or null if no such company exists.
   *
   * @param name The name of the company.
   * @return The company object if the company exists, otherwise null.
   */
  @Nullable
  public static Company getCompany(String name) {
    return Repo.getInstance().companies().getWhere(co -> co.name.equals(name));
  }

  /**
   * Creates and returns the company with the given name and player.
   *
   * @param name  The name of the company.
   * @param owner The owner of the company.
   * @return The company just created.
   * @throws StonksAPIException If a company with that name already exists.
   */
  @NotNull
  public static Company createCompany(String name, Player owner) throws StonksAPIException {
    if (getCompany(name) != null) {
      throw new StonksAPIException("Company with the given name already exists!");
    }
    return Repo.getInstance().createCompany(name, owner);
  }


  /**
   * Creates and returns the account with the given name.
   *
   * @param company     The company to create the account from.
   * @param accountName The name of the account to be created.
   * @return The account just created.
   * @throws StonksAPIException If an account with that name already exists.
   */
  @NotNull
  public static Account createAccount(Company company, String accountName) throws StonksAPIException {
    if (company.accounts.stream().anyMatch(a -> a.name.equals(accountName))) {
      throw new StonksAPIException("Account with the given name already exists!");
    }
    return Repo.getInstance().createCompanyAccount(company, accountName);
  }

  /**
   * Gets the account from a company or creates one if it's not present.
   *
   * @param company     The company to fetch/create the account from/to.
   * @param accountName The name of the account to fetch/create.
   * @return The account fetched or just created.
   * @throws StonksAPIException If something goes horribly wrong.
   */
  @NotNull
  public static Account getOrCreateAccount(Company company, String accountName) throws StonksAPIException {
    Account account = company.accounts.stream().filter(a -> a.name.equals(accountName)).findFirst().orElse(null);
    if (account == null) {
      account = createAccount(company, accountName);
    }
    return account;
  }
}
