package com.zenya.nochunklag.scheduler;

import org.bukkit.scheduler.BukkitTask;

public interface NCLTask {
    String getKey();
    void runTask();
    BukkitTask getTask();
    static NCLTask getInstance() {
        return null;
    }
}
