package dev.tycho.stonks.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

//Stores data about players in memory, stuff that doesn't need to persist between restarts
public class PlayerData {
  private static PlayerData instance;
  private HashMap<UUID, Long> createAccountCooldown = new HashMap<>();
  private HashMap<UUID, Long> createCompanyCooldown = new HashMap<>();
  private HashMap<Player, Integer> selectedCompanyChat = new HashMap<>();
  private HashMap<Player, Integer> replyCompanyChat = new HashMap<>();

  public PlayerData() {
    instance = this;
  }

  public static PlayerData getInstance() {
    if (instance == null) {
      new PlayerData();
    }
    return instance;
  }

  public HashMap<Player, Integer> getSelectedCompanyChat() {
    return selectedCompanyChat;
  }

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


  public HashMap<Player, Integer> getReplyCompanyChat() {
    return replyCompanyChat;
  }

}
