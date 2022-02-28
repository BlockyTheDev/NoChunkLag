package com.zenya.nochunklag.scheduler;

import com.zenya.nochunklag.NoChunkLag;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.cooldown.CooldownType;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.util.ChatBuilder;
import com.zenya.nochunklag.util.MetaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BoostReadyNotifyTask implements NCLTask {

    private static BoostReadyNotifyTask nclTask;
    private BukkitTask bukkitTask;
    private static CooldownManager cooldownManager;

    public BoostReadyNotifyTask(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
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
                    ChatBuilder chat = new ChatBuilder(cooldownManager).withPlayer(player).withWorld(player.getWorld());

                    if (player.hasPermission("nochunklag.notify.boostready")) {
                        if (cooldownManager.getTimer(CooldownType.ELYTRA_BOOST).getCooldown(player).equals(0) && !MetaUtils.hasMeta(player, "nochunklag.notified.elytraready")) {
                            chat.withText(MessagesManager.getInstance().getString("notifications.player.elytra-boost-ready")).sendActionBar();
                            MetaUtils.setMeta(player, "nochunklag.notified.elytraready", "");
                        }

                        if (cooldownManager.getTimer(CooldownType.TRIDENT_RIPTIDE).getCooldown(player).equals(0) && !MetaUtils.hasMeta(player, "nochunklag.notified.tridentready")) {
                            chat.withText(MessagesManager.getInstance().getString("notifications.player.trident-riptide-ready")).sendActionBar();
                            MetaUtils.setMeta(player, "nochunklag.notified.tridentready", "");
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
                    MetaUtils.setMeta(player, "nochunklag.notified.elytraready", "");
                    MetaUtils.setMeta(player, "nochunklag.notified.tridentready", "");
                }
            }
        }.runTaskAsynchronously(NoChunkLag.getInstance());
    }

    @Override
    public BukkitTask getTask() {
        return bukkitTask;
    }

    public static BoostReadyNotifyTask getInstance() {
        if (nclTask == null) {
            nclTask = new BoostReadyNotifyTask(NoChunkLag.getInstance().getCooldownManager());
        }
        return nclTask;
    }
}
