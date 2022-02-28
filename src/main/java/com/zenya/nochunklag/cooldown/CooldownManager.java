package com.zenya.nochunklag.cooldown;

import java.util.HashMap;

public class CooldownManager {

    private static final HashMap<CooldownType, CooldownTimer> COOLDOWN_TIMERS = new HashMap<>();

    public CooldownManager() {
        for (CooldownType type : CooldownType.values()) {
            addCooldownTimer(type);
        }
    }

    private void addCooldownTimer(CooldownType type) {
        COOLDOWN_TIMERS.put(type, new CooldownTimer());
    }

    public CooldownTimer getTimer(CooldownType type) {
        return COOLDOWN_TIMERS.get(type);
    }

}
