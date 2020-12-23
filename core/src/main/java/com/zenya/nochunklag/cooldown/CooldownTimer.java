package com.zenya.nochunklag.cooldown;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class CooldownTimer {
    //private CooldownTimer timer;
    private HashMap<Player, Integer> cooldowns = new HashMap<Player, Integer>();

    public void setCooldown(Player player, Integer timeLeft) {
        if(timeLeft < 1) {
            cooldowns.remove(player);
        } else {
            cooldowns.put(player, timeLeft);
        }
    }

    public Integer getCooldown(Player player) {
        return cooldowns.getOrDefault(player, 0);
    }

/*    public CooldownTimer getInstance() {
        if(timer == null) {
            timer = new CooldownTimer();
        }
        return timer;
    }*/
}
