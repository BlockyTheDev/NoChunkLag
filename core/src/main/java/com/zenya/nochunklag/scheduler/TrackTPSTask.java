package com.zenya.nochunklag.scheduler;

import com.zenya.nochunklag.NoChunkLag;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TrackTPSTask implements NCLTask {
    private static TrackTPSTask nclTask;
    private BukkitTask bukkitTask;
    private float tps = 0;

    public TrackTPSTask() {
        runTask();
    }

    @Override
    public String getKey() {
        return "TrackTPSTask";
    }

    @Override
    public void runTask() {
        bukkitTask = new BukkitRunnable() {
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

    @Override
    public BukkitTask getTask() {
        return bukkitTask;
    }

    public float getTps() {
        return tps;
    }

    public static TrackTPSTask getInstance() {
        if(nclTask == null) {
            nclTask = new TrackTPSTask();
        }
        return nclTask;
    }
}
