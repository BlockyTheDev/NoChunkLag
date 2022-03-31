package com.zenya.nochunklag.scheduler;

import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.event.ElytraBoostEvent;
import com.zenya.nochunklag.event.TridentRiptideEvent;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.util.MetaUtils;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TrackCooldownTask implements NCLTask {

    private static TrackCooldownTask nclTask;
    private BukkitTask bukkitTask;
    private Event event;

    public TrackCooldownTask(Event event) {
        this.event = event;
        runTask();
    }

    @Override
    public String getKey() {
        return "TrackCooldownTask";
    }

    @Override
    public void runTask() {
        //ElytraBoostEvent
        if (event.getEventName().equals("ElytraBoostEvent")) {
            ElytraBoostEvent e = (ElytraBoostEvent) event;

            if (ConfigManager.getInstance().getBool("tps-scale-cooldown")) {
                //Sync task
                bukkitTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        int timeLeft = e.getCooldown();
                        e.setCooldown(--timeLeft);
                        if (timeLeft == 0) {
                            //Clear meta only if cooldown is applicable to player
                            MetaUtils.clearMeta(e.getPlayer(), "nochunklag.notified.elytraready");
                        }
                        if (timeLeft <= 0) {
                            //Cancel task regardless when it expires
                            this.cancel();
                        }
                    }
                }.runTaskTimer(NoChunkLag.instance(), 0, 20);
            } else {
                //Async task
                bukkitTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        int timeLeft = e.getCooldown();
                        e.setCooldown(--timeLeft);
                        if (timeLeft == 0) {
                            //Clear meta only if cooldown is applicable to player
                            MetaUtils.clearMeta(e.getPlayer(), "nochunklag.notified.elytraready");
                        }
                        if (timeLeft <= 0) {
                            //Cancel task regardless when it expires
                            this.cancel();
                        }
                    }
                }.runTaskTimerAsynchronously(NoChunkLag.instance(), 0, 20);
            }
        }

        //TridentRiptideEvent
        if (event.getEventName().equals("TridentRiptideEvent")) {
            TridentRiptideEvent e = (TridentRiptideEvent) event;

            if (ConfigManager.getInstance().getBool("tps-scale-cooldown")) {
                //Sync task
                bukkitTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        int timeLeft = e.getCooldown();
                        e.setCooldown(--timeLeft);
                        if (timeLeft == 0) {
                            //Clear meta only if cooldown is applicable to player
                            MetaUtils.clearMeta(e.getPlayer(), "nochunklag.notified.tridentready");
                        }
                        if (timeLeft <= 0) {
                            //Cancel task regardless when it expires
                            this.cancel();
                        }
                    }
                }.runTaskTimer(NoChunkLag.instance(), 0, 20);
            } else {
                //Async task
                bukkitTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        int timeLeft = e.getCooldown();
                        e.setCooldown(--timeLeft);
                        if (timeLeft == 0) {
                            //Clear meta only if cooldown is applicable to player
                            MetaUtils.clearMeta(e.getPlayer(), "nochunklag.notified.tridentready");
                        }
                        if (timeLeft <= 0) {
                            //Cancel task regardless when it expires
                            this.cancel();
                        }
                    }
                }.runTaskTimerAsynchronously(NoChunkLag.instance(), 0, 20);
            }
        }
    }

    @Override
    public BukkitTask getTask() {
        return bukkitTask;
    }
}
