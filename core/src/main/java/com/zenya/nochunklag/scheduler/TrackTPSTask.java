package com.zenya.nochunklag.scheduler;

import com.zenya.nochunklag.NoChunkLag;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class TrackTPSTask implements NCLTask {
    private static TrackTPSTask nclTask;
    private BukkitTask bukkitTask;
    private float instTps = 0;
    private float avgTps = 0;

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
                    instTps = (float) (1000/tdiff);
                }
            }
        }.runTaskTimer(NoChunkLag.getInstance(), 0, 1);

        //Task to populate avgTps
        new BukkitRunnable() {
            ArrayList<Float> tpsList = new ArrayList<Float>();

            @Override
            public void run() {
                Float totalTps = 0f;

                tpsList.add(instTps);
                //Remove old tps after 15s
                if(tpsList.size() >= 15) {
                    tpsList.remove(0);
                }
                for(Float f : tpsList) {
                    totalTps += f;
                }
                avgTps = totalTps / tpsList.size();
            }
        }.runTaskTimerAsynchronously(NoChunkLag.getInstance(), 20, 20);
    }

    @Override
    public BukkitTask getTask() {
        return bukkitTask;
    }

    public float getInstantTps() {
        if(instTps > 20.0f) {
            instTps = 20.0f;
        }
        return instTps;
    }

    public float getAverageTps() {
        if(avgTps > 20.0f) {
            avgTps = 20.0f;
        }
        return avgTps;
    }

    public static TrackTPSTask getInstance() {
        if(nclTask == null) {
            nclTask = new TrackTPSTask();
        }
        return nclTask;
    }
}
