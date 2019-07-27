package nl.tychovi.stonks.model;

import java.util.ArrayList;
import java.util.List;

public class Company {

    List<Account> accounts = new ArrayList<Account>();
    String name;

    public String getName() {
        return name;
    }
    public boolean setName(String name) {
        //Don't allow blank names
        if (name.isBlank()) {
            return false;
        }
        this.name = name;
        return true;
    }




}
