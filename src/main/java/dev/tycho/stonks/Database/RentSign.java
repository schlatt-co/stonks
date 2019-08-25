package dev.tycho.stonks.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@DatabaseTable(tableName = "rentsign")
public class RentSign {
    @DatabaseField
    private double x;

    @DatabaseField
    private double y;

    @DatabaseField
    private double z;


    public RentSign() {}

    public RentSign(int accountId, Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.y = location.getY();
    }
}
