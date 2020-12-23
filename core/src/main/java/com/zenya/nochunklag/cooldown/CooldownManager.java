package com.zenya.nochunklag.cooldown;

import java.util.HashMap;

public class CooldownManager {
    private static CooldownManager cdm;
    private static HashMap<CooldownType, CooldownTimer> cdmMap = new HashMap<CooldownType, CooldownTimer>();

    public CooldownManager() {
        cdmMap.put(CooldownType.ELYTRA_BOOST, new CooldownTimer());
        cdmMap.put(CooldownType.TRIDENT_RIPTIDE, new CooldownTimer());
    }

    public CooldownTimer getTimer(CooldownType type) {
        return cdmMap.get(type);
    }

    public static CooldownManager getInstance() {
        if(cdm == null) {
            cdm = new CooldownManager();
        }
        return cdm;
    }
}
