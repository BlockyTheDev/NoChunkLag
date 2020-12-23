package com.zenya.nochunklag.util;

import com.zenya.nochunklag.NoChunkLag;
import org.bukkit.scheduler.BukkitRunnable;

public class TPSTracker {
    private static TPSTracker tpsTracker;
    private float tps = 0;

    public TPSTracker() {
        track();
    }

    private void track() {
        new BukkitRunnable() {
            long start = 0;
            long now = 0;

            @Override
            public void run() {
                start = now;
                now = System.currentTimeMillis();
                long tdiff = now - start;

                if(tdiff > 0) {
                    tps = (float) (1000/tdiff);
                }
                if(tps > 20.0f) {
                    tps = 20.0f;
                }
            }
        }.runTaskTimer(NoChunkLag.getInstance(), 0, 1);
    }

    public float getTps() {
        return tps;
    }

    public static TPSTracker getInstance() {
        if(tpsTracker == null) {
            tpsTracker = new TPSTracker();
        }
        return tpsTracker;
    }
}
