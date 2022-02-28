package com.zenya.nochunklag;

import com.zenya.nochunklag.command.NoChunkLagCommand;
import com.zenya.nochunklag.command.NoChunkLagTab;
import com.zenya.nochunklag.cooldown.CooldownManager;
import com.zenya.nochunklag.event.LegacyListeners;
import com.zenya.nochunklag.event.ModernListeners;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.scheduler.TaskManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.nochunklag.MetricsLite;
import optic_fusion1.nochunklag.Updater;
import optic_fusion1.nochunklag.Updater.UpdateResult;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class NoChunkLag extends JavaPlugin {

    private static NoChunkLag instance;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        new MetricsLite(this, 13820);
        checkForUpdate();

        cooldownManager = new CooldownManager();

        //Register all runnables
        TaskManager.getInstance();

        //Init config and messages
        ConfigManager.getInstance();
        MessagesManager.getInstance();

        //Register events
        this.getServer().getPluginManager().registerEvents(new LegacyListeners(), this);
        //PlayerRiptideEvent only available post-aquatic update (>=1.13)
        this.getServer().getPluginManager().registerEvents(new ModernListeners(), this);

        //Register commands
        this.getCommand("nochunklag").setExecutor(new NoChunkLagCommand(cooldownManager));
        this.getCommand("nochunklag").setTabCompleter(new NoChunkLagTab());
    }

    @Override
    public void onDisable() {

    }

    public static NoChunkLag getInstance() {
        return instance;
    }

    private void checkForUpdate() {
        Logger logger = getLogger();
        FileConfiguration pluginConfig = getConfig();
        Updater updater = new Updater(this, 98913, false);
        Updater.UpdateResult result = updater.getResult();
        if (result != UpdateResult.UPDATE_AVAILABLE) {
            return;
        }
        if (!pluginConfig.getBoolean("download-update")) {
            logger.info("===== UPDATE AVAILABLE ====");
            logger.info("https://www.spigotmc.org/resources/98913");
            logger.log(Level.INFO, "Installed Version: {0} New Version:{1}", new Object[]{updater.getOldVersion(), updater.getVersion()});
            logger.info("===== UPDATE AVAILABLE ====");
            return;
        }
        logger.info("==== UPDATE AVAILABLE ====");
        logger.info("====    DOWNLOADING   ====");
        updater.downloadUpdate();
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
