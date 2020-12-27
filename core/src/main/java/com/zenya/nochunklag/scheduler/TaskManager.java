package com.zenya.nochunklag.scheduler;

import java.util.HashMap;

public class TaskManager {
    private static TaskManager taskManager;
    private HashMap<String, NCLTask> taskMap = new HashMap<String, NCLTask>();

    public TaskManager() {
        registerTask(TrackTPSTask.getInstance().getKey(), TrackTPSTask.getInstance());
        registerTask(BoostReadyNotifyTask.getInstance().getKey(), BoostReadyNotifyTask.getInstance());
        registerTask(LowTPSNotifyTask.getInstance().getKey(), LowTPSNotifyTask.getInstance());
    }

    public NCLTask getTask(String key) {
        return taskMap.getOrDefault(key, null);
    }

    public void registerTask(String key, NCLTask task) {
        taskMap.put(key, task);
    }

    public void unregisterTask(String key) {
        taskMap.remove(key);
    }

    public static TaskManager getInstance() {
        if(taskManager == null) {
            taskManager = new TaskManager();
        }
        return taskManager;
    }
}
