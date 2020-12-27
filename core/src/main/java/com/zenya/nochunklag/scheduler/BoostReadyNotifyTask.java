package com.zenya.nochunklag.scheduler;

import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.util.ChatUtils;
import com.zenya.nochunklag.util.MetaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;

public class BoostReadyNotifyTask implements NCLTask {
    private static BoostReadyNotifyTask nclTask;
    private BukkitTask bukkitTask;
    private MessagesManager messagesManager = MessagesManager.getInstance();
    private MetaUtils metaUtils = MetaUtils.getInstance();
    private static CooldownManager cdm = CooldownManager.getInstance();

    public BoostReadyNotifyTask() {
        runTask();
    }

    @Override
    public String getKey() {
        return "BoostReadyNotifyTask";
    }

    @Override
    public void runTask() {
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("nochunklag.notify.boostready")) {
                        if (cdm.getTimer(CooldownType.ELYTRA_BOOST).getCooldown(player).equals(0) && !metaUtils.hasMeta(player, "nochunklag.notified.elytraready")) {
                            ChatUtils.sendActionBar(player, messagesManager.getString("notifications.player.elytra-boost-ready"));
                            metaUtils.setMeta(player, "nochunklag.notified.elytraready", "");
                        }

                        if (cdm.getTimer(CooldownType.TRIDENT_RIPTIDE).getCooldown(player).equals(0) && !metaUtils.hasMeta(player, "nochunklag.notified.tridentready")) {
                            ChatUtils.sendActionBar(player, messagesManager.getString("notifications.player.trident-riptide-ready"));
                            metaUtils.setMeta(player, "nochunklag.notified.tridentready", "");
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(NoChunkLag.getInstance(), 10, 10);


        //One-time task to set meta for everyone online in case of plugman reload
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    metaUtils.setMeta(player, "nochunklag.notified.elytraready", "");
                    metaUtils.setMeta(player, "nochunklag.notified.tridentready", "");
                }
            }
        }.runTaskAsynchronously(NoChunkLag.getInstance());
    }

    @Override
    public BukkitTask getTask() {
        return bukkitTask;
    }

    public static BoostReadyNotifyTask getInstance() {
        if(nclTask == null) {
            nclTask = new BoostReadyNotifyTask();
        }
        return nclTask;
    }
}
