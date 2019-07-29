package nl.tychovi.stonks.model;

import java.util.UUID;

public class Membership {
    private UUID player_uuid;
    private Permissions permissions;

    public UUID getPlayerUuid() {
        return player_uuid;
    }

    public void setPlayerUuid(UUID player_uuid) {
        this.player_uuid = player_uuid;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }
}


