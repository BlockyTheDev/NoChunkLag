package com.zenya.nochunklag;

import com.zenya.nochunklag.event.Listeners;
import com.zenya.nochunklag.file.ConfigManager;
import com.zenya.nochunklag.file.MessagesManager;
import com.zenya.nochunklag.nms.CompatibilityHandler;
import com.zenya.nochunklag.nms.ProtocolNMS;
import com.zenya.nochunklag.util.TPSTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class NoChunkLag extends JavaPlugin {
    private static NoChunkLag instance;
    private static CompatibilityHandler compatibilityHandler = CompatibilityHandler.getInstance();
    private static ProtocolNMS protocolNMS;
    private static TPSTracker tpsTracker;

    public void onEnable() {
        instance = this;

        try {
            protocolNMS = compatibilityHandler.getProtocolNMS().newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Spigot NMS version " + compatibilityHandler.getVersion() + " is not supported by NoChunkLag");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }

        tpsTracker = TPSTracker.getInstance();

        try {
            ConfigManager.getInstance();
            MessagesManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    public void onDisable() {

    }

    public static ProtocolNMS getProtocolNMS() {
        return protocolNMS;
    }

    public static TPSTracker getTPSTracker() {
        return tpsTracker;
    }

    public static NoChunkLag getInstance() {
        return instance;
    }
}
