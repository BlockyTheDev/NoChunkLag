package com.zenya.nochunklag.scheduler;

import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.util.ChatUtils;
import com.zenya.nochunklag.util.MetaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LowTPSNotifyTask implements NCLTask {
    private static LowTPSNotifyTask nclTask;
    private BukkitTask bukkitTask;
    private MetaUtils metaUtils = MetaUtils.getInstance();
    private TrackTPSTask trackTPSTask = TrackTPSTask.getInstance();

    public LowTPSNotifyTask() {
        runTask();
    }

    @Override
    public String getKey() {
        return "LowTPSNotifyTask";
    }

    @Override
    public void runTask() {
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                float tps = trackTPSTask.getAverageTps();

                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.hasPermission("nochunklag.notify.lowtps")) {
                        if(tps < ConfigManager.getInstance().getInt("noboost-tps-treshold") && !metaUtils.hasMeta(player, "nochunklag.notified.lowtps")) {
                            ChatUtils.sendMessage(player, MessagesManager.getInstance().getString("notifications.admin.tps-low"));
                            metaUtils.setMeta(player, "nochunklag.notified.lowtps", "");
                            metaUtils.clearMeta(player, "nochunklag.notified.regtps");
                        }

                        if(tps > ConfigManager.getInstance().getInt("noboost-tps-treshold") && !metaUtils.hasMeta(player, "nochunklag.notified.regtps")) {
                            ChatUtils.sendMessage(player, MessagesManager.getInstance().getString("notifications.admin.tps-regular"));
                            metaUtils.setMeta(player, "nochunklag.notified.regtps", "");
                            metaUtils.clearMeta(player, "nochunklag.notified.lowtps");
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(NoChunkLag.getInstance(), 100, 100);

        //One-time task to set meta for everyone online in case of plugman reload
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    metaUtils.setMeta(player, "nochunklag.notified.regtps", "");
                }
            }
        }.runTaskAsynchronously(NoChunkLag.getInstance());
    }

    @Override
    public BukkitTask getTask() {
        return bukkitTask;
    }

    public static LowTPSNotifyTask getInstance() {
        if(nclTask == null) {
            nclTask = new LowTPSNotifyTask();
        }
        return nclTask;
    }

}
