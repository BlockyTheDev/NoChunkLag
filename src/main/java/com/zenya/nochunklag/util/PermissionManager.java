package com.zenya.nochunklag.util;

import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.ConfigManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PermissionManager {

  private Player player;
  private CooldownType cooldownType;

  public PermissionManager(Player player, CooldownType cooldownType) {
    this.player = player;
    this.cooldownType = cooldownType;
  }

  public ArrayList<String> getGroups() {
    ArrayList<String> groups = new ArrayList<>();
    groups.addAll(ConfigManager.getInstance().getKeys("groups"));
    return groups;
  }

  public String getGroup() {
    String topGroup = "default";
    for (String checkGroup : getGroups()) {
      if (player.hasPermission("nochunklag.group." + checkGroup)) {
        topGroup = checkGroup;
      }
    }
    return topGroup;
  }

  public int getGroupCooldown() {
    return ConfigManager.getInstance().getInt("groups." + getGroup() + ".cooldowns." + cooldownType.toString().replace('_', '-').toLowerCase());
  }

  //Magic numbers ._.
  public double getGroupSpeedMultiplier() {
    double baseMultiplier = ConfigManager.getInstance().getDouble("groups." + getGroup() + ".speed-multiplier." + cooldownType.toString().replace('_', '-').toLowerCase());
    return switch (cooldownType) {
      case ELYTRA_BOOST ->
        baseMultiplier * 1.5d;
      case TRIDENT_RIPTIDE ->
        baseMultiplier * 0.65d;
      default ->
        baseMultiplier;
    };
  }

  public int getGroupDurabilityLoss() {
    return ConfigManager.getInstance().getInt("groups." + getGroup() + ".additional-durability-loss." + cooldownType.toString().replace('_', '-').toLowerCase());
  }
}
