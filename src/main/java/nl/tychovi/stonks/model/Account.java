package nl.tychovi.stonks.model;

public class Account {
    String name;

    public String GetName() {
        return name;
    }
    public boolean SetName(String name) {
        //Don't allow blank names
        if (name.isBlank()) {
            return false;
        }
        this.name = name;
        return true;
    }
}
