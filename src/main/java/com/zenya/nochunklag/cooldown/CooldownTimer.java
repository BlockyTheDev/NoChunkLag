package com.zenya.nochunklag.cooldown;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class CooldownTimer {

    private HashMap<Player, Integer> cooldowns = new HashMap<>();

    public void setCooldown(Player player, Integer timeLeft) {
        if (!(timeLeft < 1)) {
            cooldowns.put(player, timeLeft);
            return;
        }
        cooldowns.remove(player);
    }

    public Integer getCooldown(Player player) {
        return cooldowns.getOrDefault(player, 0);
    }
}
