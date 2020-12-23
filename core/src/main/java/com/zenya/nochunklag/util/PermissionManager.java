package com.zenya.nochunklag.util;

import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.ConfigManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PermissionManager {
    private static ConfigManager configManager = ConfigManager.getInstance();
    private Player player;
    private CooldownType cooldownType;

    public PermissionManager(Player player, CooldownType cooldownType) {
        this.player = player;
        this.cooldownType = cooldownType;
    }

    public ArrayList<String> getGroups() {
        ArrayList<String> groups = new ArrayList<String>();
        groups.addAll(configManager.getKeys("groups"));
        return groups;
    }

    public String getGroup() {
        String topGroup = "default";
        for(String checkGroup : getGroups()) {
            if(player.hasPermission("nochunklag.group." + checkGroup)) {
                topGroup = checkGroup;
            }
        }
        return topGroup;
    }

    public int getGroupCooldown() {
        return configManager.getInt("groups." + getGroup() + ".cooldowns." + cooldownType.toString().replace('_', '-').toLowerCase());
    }

    //Magic numbers ._.
    public double getGroupSpeedMultiplier() {
        double baseMultiplier = configManager.getDouble( "groups." + getGroup() + ".speed-multiplier." + cooldownType.toString().replace('_', '-').toLowerCase());
        switch(cooldownType) {
            case ELYTRA_BOOST:
                return baseMultiplier * 1.5d;
            case TRIDENT_RIPTIDE:
                return baseMultiplier * 0.65d;
            default:
                return baseMultiplier;
        }
    }

    public int getGroupDurabilityLoss() {
        return configManager.getInt("groups." + getGroup() + ".additional-durability-loss." + cooldownType.toString().replace('_', '-').toLowerCase());
    }
}