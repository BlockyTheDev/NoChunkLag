package com.zenya.nochunklag;

import com.zenya.nochunklag.command.NoChunkLagCommand;
import com.zenya.nochunklag.event.LegacyListeners;
import com.zenya.nochunklag.event.ModernListeners;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.nms.CompatibilityHandler;
import com.zenya.nochunklag.nms.ProtocolNMS;
import com.zenya.nochunklag.scheduler.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class NoChunkLag extends JavaPlugin {
    private static NoChunkLag instance;
    private static ProtocolNMS protocolNMS;

    public void onEnable() {
        instance = this;

        //Set NMS version
        try {
            protocolNMS = CompatibilityHandler.getProtocolNMS().newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Spigot NMS version " + CompatibilityHandler.getVersion() + " is not supported by NoChunkLag");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }

        //Register all runnables
        TaskManager.getInstance();

        //Init config and messages
        ConfigManager.getInstance();
        MessagesManager.getInstance();

        //Register events
        this.getServer().getPluginManager().registerEvents(new LegacyListeners(), this);
        //PlayerRiptideEvent only available post-aquatic update (>=1.13)
        if(CompatibilityHandler.getProtocol() >= 13) {
            this.getServer().getPluginManager().registerEvents(new ModernListeners(), this);
        }

        //Register commands
        this.getCommand("nochunklag").setExecutor(new NoChunkLagCommand());
    }

    public void onDisable() {

    }

    public static ProtocolNMS getProtocolNMS() {
        return protocolNMS;
    }

    public static NoChunkLag getInstance() {
        return instance;
    }
}
