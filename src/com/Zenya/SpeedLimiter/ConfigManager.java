package com.Zenya.SpeedLimiter;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import sun.security.krb5.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigManager {
	
	Plugin plugin;
	FileConfiguration config;
	private static ConfigManager configManager;
	
	private ConfigManager() {
		this.plugin = Main.instance;
		this.config = plugin.getConfig();

		if(!(this.getConfigExists())) {
			plugin.saveDefaultConfig();
		}

		if(this.getConfigVersion() != 1) {
			File configFile = new File(plugin.getDataFolder(), "config.yml");
			File oldConfigFile = new File(plugin.getDataFolder(), "config.yml.old");

			configFile.renameTo(oldConfigFile);
			configFile.delete();

			plugin.saveDefaultConfig();
		}
	}
	
	public boolean getConfigExists() {
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		return configFile.exists();
	}

	public int getConfigVersion() {
		int iver;
		if(!config.contains("config-version") || config.get("config-version") == null) {
			iver = 0;
		} else {
			iver = config.getInt("config-version");
		}
		return iver;
	}

	public int getCooldown(String type) {
		return config.getInt(type.toLowerCase() + "-cooldown");
	}

	public String getMessage(String type, Integer time) {
		return ChatColor.translateAlternateColorCodes('&', config.getString(type.toLowerCase() + "-cooldown").replace("%time%", time.toString()));
	}
	
	public static ConfigManager getInstance() {
		if(configManager == null) {
			configManager = new ConfigManager();
		}
		return configManager;
	}

}
