package dev.tycho.stonks.managers;

import java.util.HashMap;
import java.util.UUID;

//Stores data about players in memory, stuff that doesn't need to persist between restarts
public class PlayerStateData {
  private static PlayerStateData instance;
  public static PlayerStateData getInstance() {
    if (instance == null) {
      new PlayerStateData();
    }
    return instance;
  }

  public PlayerStateData() {
    instance = this;
  }

  private HashMap<UUID, Long> createAccountCooldown = new HashMap<>();
  private HashMap<UUID, Long> createCompanyCooldown = new HashMap<>();


  public void setPlayerCreateAccountCooldown(UUID playerUUID, long cooldown) {
    createAccountCooldown.put(playerUUID, cooldown);
  }
  public void setPLayerCreateCompanyCooldown(UUID playerUUID, long cooldown) {
    createCompanyCooldown.put(playerUUID, cooldown);
  }

  public long getPlayerCreateAccountCooldown(UUID playerUUID) {
    if (createAccountCooldown.containsKey(playerUUID)) return createAccountCooldown.get(playerUUID);
    return 0;
  }

  public long getPlayerCreateCompanyCooldown(UUID playerUUID) {
    if (createCompanyCooldown.containsKey(playerUUID)) return createCompanyCooldown.get(playerUUID);
    return 0;
  }



}
